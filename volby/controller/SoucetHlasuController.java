package volby.controller;

import com.sun.javafx.beans.event.AbstractNotifyListener;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import volby.Aplikace;
import volby.Databaze;
import volby.Gui;
import volby.MyComboBoxTableCell;
import volby.NevybranZaznamException;
import volby.model.Hlas;
import volby.model.Kandidat;
import volby.model.Kraj;
import volby.model.Okrsek;
import volby.model.PreferencniHlas;
import volby.model.Strana;


public class SoucetHlasuController implements Initializable {
    @FXML
    private Label uzivatel;
    
    @FXML
    private TableView<Hlas> tableHlasy;
    
    @FXML
    private TableView<PreferencniHlas> tablePreferencniHlasy;
    
    @FXML
    private ComboBox<Kraj> comboBoxKraje;
    
    @FXML
    private ComboBox<Okrsek> comboBoxOkrsky;
    
    @FXML
    private TableColumn<Hlas, String> casVytvoreniHlasuColumn;
    
    @FXML
    private TableColumn<Hlas, Strana> hlasStraneColumn;
    
    @FXML
    private TableColumn<PreferencniHlas, Kandidat> preferencniHlasColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            uzivatel.setText(Databaze.getUzivatel().getJmeno() + " " + Databaze.getUzivatel().getPrijmeni());
            comboBoxKraje.setItems(Databaze.nactiKraje());
            comboBoxKraje.getSelectionModel().selectedItemProperty().addListener(new AbstractNotifyListener() {
                @Override
                public void invalidated(Observable observable) {
                    try {
                        comboBoxOkrsky.setItems(Databaze.nactiOkrsky(aktualniKraj()));
                    } catch (Exception e) {}
                }
            });
            try {
                comboBoxOkrsky.setItems(Databaze.nactiOkrsky(aktualniKraj()));
            } catch(NevybranZaznamException e){
                comboBoxOkrsky.setItems(null);
            }
            
            casVytvoreniHlasuColumn.setCellValueFactory(new PropertyValueFactory<>("casVytvoreniText"));
            casVytvoreniHlasuColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            
            hlasStraneColumn.setCellValueFactory(new PropertyValueFactory<>("strana"));
            hlasStraneColumn.setCellFactory(MyComboBoxTableCell.forTableColumn(Databaze.nactiStrany()));
            hlasStraneColumn.setOnEditCommit(onStranaHlasuEdited);
            
            preferencniHlasColumn.setCellValueFactory(new PropertyValueFactory<>("kandidat"));
            aktualizovatKandidatiComboBox();
            preferencniHlasColumn.setOnEditCommit(onKandidatHlasuEdited);
            
            tableHlasy.getSelectionModel().selectedItemProperty().addListener(new AbstractNotifyListener() {
                @Override
                public void invalidated(Observable observable) {
                    try {
                        aktualizovatKandidatiComboBox();
                        aktualizovatPreferencniHlasy();
                    } catch (Exception e) {
                        Gui.zobrazitVarovani("Chyba", "Při načítání dat se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                    }
                }
            });
            
            tableHlasy.setPlaceholder(new Label("Tabulka neobsahuje data."));
            tablePreferencniHlasy.setPlaceholder(new Label("Tabulka neobsahuje data."));
            
            comboBoxKraje.setPromptText("<vyberte hodnotu>");
            comboBoxOkrsky.setPromptText("<vyberte hodnotu>");
            
            aktualizovat();
        } catch(Exception e){
            Gui.zobrazitVarovani("Chyba", "Při vytváření stránky se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    private void aktualizovat() {
        try {
            aktualizovatHlasy();
            aktualizovatPreferencniHlasy();
        } catch (Exception e) {
            Gui.zobrazitVarovani("Chyba", "Při aktualizaci tabulky se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    private void aktualizovatHlasy() throws Exception {
        TableColumn sortcolumn = null;
        SortType st = null;
        if (tableHlasy.getSortOrder().size() > 0) {
            sortcolumn = (TableColumn) tableHlasy.getSortOrder().get(0);
            st = sortcolumn.getSortType();
        }
        
        int vybranyRadek = 0;
        vybranyRadek = tableHlasy.getSelectionModel().getSelectedIndex();
        try {
            tableHlasy.setItems(Databaze.nactiHlasy(aktualniOkrsek()));
        } catch(NevybranZaznamException e){
            tableHlasy.setItems(null);
        }
        
        
        if (sortcolumn != null) {
            tableHlasy.getSortOrder().add(sortcolumn);
            sortcolumn.setSortType(st);
            sortcolumn.setSortable(true);
        }
        
        if(vybranyRadek >= 0) tableHlasy.getSelectionModel().select(vybranyRadek);
    }
    
    private void aktualizovatPreferencniHlasy() throws Exception {
        try {
            tablePreferencniHlasy.setItems(Databaze.nactiPreferencniHlasy(aktualniHlas()));
        } catch(NevybranZaznamException e){
            tablePreferencniHlasy.setItems(null);
        }
    }
    
    private void aktualizovatKandidatiComboBox() throws Exception {
        try {
            preferencniHlasColumn.setCellFactory(MyComboBoxTableCell.forTableColumn(Databaze.nactiKandidaty(aktualniHlas().getStrana())));
        } catch(NevybranZaznamException e){
            preferencniHlasColumn.setCellFactory(MyComboBoxTableCell.forTableColumn());
        }
    }
    
    private Hlas aktualniHlas() throws NevybranZaznamException {
        Hlas hlas = tableHlasy.getSelectionModel().getSelectedItem();
        if(hlas == null) throw new NevybranZaznamException();
        return hlas;
    }
    
    private PreferencniHlas aktualniPreferencniHlas() throws NevybranZaznamException {
        PreferencniHlas hlas = tablePreferencniHlasy.getSelectionModel().getSelectedItem();
        if(hlas == null) throw new NevybranZaznamException();
        return hlas;
    }
    
    private Okrsek aktualniOkrsek() throws NevybranZaznamException {
        Okrsek okrsek = comboBoxOkrsky.getSelectionModel().getSelectedItem();
        if(okrsek == null) throw new NevybranZaznamException();
        return okrsek;
    }
    
    private Kraj aktualniKraj() throws NevybranZaznamException {
        Kraj kraj = comboBoxKraje.getSelectionModel().getSelectedItem();
        if(kraj == null) throw new NevybranZaznamException();
        return kraj;
    }
    
    @FXML
    private void odhlasit(){
        try {
            if(Databaze.isProvedenaZmena()){
                int akce = Gui.zobrazitDialogUlozeni();
                if(akce == Gui.ZPET) return;
                if(akce == Gui.ULOZIT_ZMENY) Databaze.ulozitZmeny();
                if(akce == Gui.ZAHODIT_ZMENY) Databaze.zahoditZmeny();
            }
            Databaze.odhlasit();
            Aplikace.zmenaStranky("Prihlaseni.fxml");
        } catch(Exception e){
            Gui.zobrazitVarovani("Chyba", "Při odhlašování se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    @FXML
    private void novyHlas(){
        try {
            Databaze.novyHlas(aktualniOkrsek());
            aktualizovat();
        } catch(Exception e){
            Gui.zobrazitVarovani("Chyba", "Při vytváření záznamu se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    @FXML
    private void novyPreferencniHlas(){
        try {
            Databaze.novyPreferencniHlas(aktualniHlas());
            aktualizovat();
        } catch(Exception e){
            Gui.zobrazitVarovani("Chyba", "Při vytváření záznamu se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    @FXML
    private void odstranitHlas(){
        try {
            Databaze.odstranitHlas(aktualniHlas());
            aktualizovat();
        } catch(NevybranZaznamException e){
            Gui.zobrazitVarovani("Pozor", "Nebyl vybrán žádný záznam.");
        } catch(Exception e){
            Gui.zobrazitVarovani("Chyba", "Při odstraňování záznamu se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    @FXML
    private void odstranitPreferencniHlas(){
        try {
            Databaze.odstranitPreferencniHlas(aktualniPreferencniHlas());
            aktualizovat();
        } catch(NevybranZaznamException e){
            Gui.zobrazitVarovani("Pozor", "Nebyl vybrán žádný záznam.");
        } catch(Exception e){
            Gui.zobrazitVarovani("Chyba", "Při odstraňování záznamu se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    @FXML
    private void ulozit(){
        try {
            Databaze.ulozitZmeny();
            Gui.zobrazitInformaci("Změny uloženy", "Změny byly úspěšně uloženy!");
        } catch(Exception e){
            Gui.zobrazitVarovani("Chyba", "Při ukládání se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    @FXML
    private void zmenaFiltru(){
        try {
            aktualizovat();
        } catch (Exception e) {
            Gui.zobrazitVarovani("Chyba", "Při načítání dat se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    private EventHandler<TableColumn.CellEditEvent<Hlas, Strana>> onStranaHlasuEdited = new EventHandler<TableColumn.CellEditEvent<Hlas, Strana>>() {
        @Override
        public void handle(TableColumn.CellEditEvent<Hlas, Strana> event) {
            try {
                Hlas hlas = event.getTableView().getItems().get(event.getTablePosition().getRow());
                if(hlas.getStranaID() != event.getNewValue().getId()){
                    hlas.setStranaID(event.getNewValue().getId());
                    Databaze.upravitHlas(hlas);
                    aktualizovatKandidatiComboBox();
                }
            } catch(Exception e){
                Gui.zobrazitVarovani("Chyba", "Vyskytla se chyba při změně hodnoty: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    };
    
    private EventHandler<TableColumn.CellEditEvent<PreferencniHlas, Kandidat>> onKandidatHlasuEdited = new EventHandler<TableColumn.CellEditEvent<PreferencniHlas, Kandidat>>() {
        @Override
        public void handle(TableColumn.CellEditEvent<PreferencniHlas, Kandidat> event) {
            try {
                PreferencniHlas hlas = event.getTableView().getItems().get(event.getTablePosition().getRow());
                if(hlas.getKandidatID() != event.getNewValue().getId()){
                    hlas.setKandidatID(event.getNewValue().getId());
                    Databaze.upravitPreferencniHlas(hlas);
                }
            } catch(Exception e){
                Gui.zobrazitVarovani("Chyba", "Vyskytla se chyba při změně hodnoty: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    };
}
