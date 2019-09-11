package volby.controller;

import com.sun.javafx.beans.event.AbstractNotifyListener;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import volby.Aplikace;
import volby.Databaze;
import volby.Gui;
import volby.NevybranZaznamException;
import volby.model.Kraj;
import volby.model.Okrsek;


public class SpravaOkrskuController implements Initializable {
    @FXML
    private Label uzivatel;
    
    @FXML
    private TableView<Kraj> tableKraje;
    
    @FXML
    private TableView<Okrsek> tableOkrsky;
    
    @FXML
    private TableColumn<Kraj, String> nazevKrajeColumn;
    
    @FXML
    private TableColumn<Okrsek, String> cisloOkrskuColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            uzivatel.setText(Databaze.getUzivatel().getJmeno() + " " + Databaze.getUzivatel().getPrijmeni());
            
            nazevKrajeColumn.setCellValueFactory(new PropertyValueFactory<>("nazev"));
            nazevKrajeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            nazevKrajeColumn.setOnEditCommit(onNazevKrajeEdited);
            
            cisloOkrskuColumn.setCellValueFactory(new PropertyValueFactory<>("cisloText"));
            cisloOkrskuColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            cisloOkrskuColumn.setOnEditCommit(onCisloOkrskuEdited);
            
            tableKraje.setPlaceholder(new Label("Tabulka neobsahuje data."));
            tableOkrsky.setPlaceholder(new Label("Tabulka neobsahuje data."));
            
            aktualizovat();
            
            tableKraje.getSelectionModel().selectedItemProperty().addListener(new AbstractNotifyListener() {
                @Override
                public void invalidated(Observable observable) {
                    try {
                        aktualizovatOkrsky();
                    } catch (Exception e) {
                        Gui.zobrazitVarovani("Chyba", "Při aktualizaci nastala chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                    }
                }
            });
        } catch(Exception e){
            Gui.zobrazitVarovani("Chyba", "Při vytváření stránky se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    private void aktualizovat() {
        try {
            aktualizovatKraje();
            aktualizovatOkrsky();
        } catch (Exception e) {
            Gui.zobrazitVarovani("Chyba", "Při aktualizaci tabulky se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    private void aktualizovatKraje() throws Exception {
        TableColumn sortcolumn = null;
        SortType st = null;
        if (tableKraje.getSortOrder().size() > 0) {
            sortcolumn = (TableColumn) tableKraje.getSortOrder().get(0);
            st = sortcolumn.getSortType();
        }
        
        int vybranyRadek = 0;
        vybranyRadek = tableKraje.getSelectionModel().getSelectedIndex();
        tableKraje.setItems(Databaze.nactiKraje());
        
        if (sortcolumn != null) {
            tableKraje.getSortOrder().add(sortcolumn);
            sortcolumn.setSortType(st);
            sortcolumn.setSortable(true);
        }
        
        if(vybranyRadek >= 0) tableKraje.getSelectionModel().select(vybranyRadek);
    }
    
    private void aktualizovatOkrsky() throws Exception {
        try {
            tableOkrsky.setItems(Databaze.nactiOkrsky(aktualniKraj()));
        } catch(NevybranZaznamException e){
            tableOkrsky.setItems(null);
        }
    }
    
    @FXML
    private void zpet(){
        try {
            if(Databaze.isProvedenaZmena()){
                int akce = Gui.zobrazitDialogUlozeni();
                if(akce == Gui.ZPET) return;
                if(akce == Gui.ULOZIT_ZMENY) Databaze.ulozitZmeny();
                if(akce == Gui.ZAHODIT_ZMENY) Databaze.zahoditZmeny();
            }
            Aplikace.zmenaStranky("MenuSpravaUdaju.fxml");
        } catch(Exception e){
            Gui.zobrazitVarovani("Chyba", "Při odhlašování se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    @FXML
    private void novyKraj(){
        try {
            Databaze.novyKraj();
            aktualizovat();
        } catch(Exception e){
            Gui.zobrazitVarovani("Chyba", "Při vytváření záznamu se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    @FXML
    private void novyOkrsek(){
        try {
            Databaze.novyOkrsek(aktualniKraj());
            aktualizovat();
        } catch(Exception e){
            Gui.zobrazitVarovani("Chyba", "Při vytváření záznamu se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    @FXML
    private void odstranitKraj(){
        try {
            Databaze.odstranitKraj(aktualniKraj());
            aktualizovat();
        } catch(NevybranZaznamException e){
            Gui.zobrazitVarovani("Pozor", "Nebyl vybrán žádný záznam.");
        } catch(Exception e){
            Gui.zobrazitVarovani("Chyba", "Při odstraňování záznamu se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    @FXML
    private void odstranitOkrsek(){
        try {
            Databaze.odstranitOkrsek(aktualniOkrsek());
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
    
    private Kraj aktualniKraj() throws NevybranZaznamException {
        Kraj kraj = tableKraje.getSelectionModel().getSelectedItem();
        if(kraj == null) throw new NevybranZaznamException();
        return kraj;
    }
    
    private Okrsek aktualniOkrsek() throws NevybranZaznamException {
        Okrsek okrsek = tableOkrsky.getSelectionModel().getSelectedItem();
        if(okrsek == null) throw new NevybranZaznamException();
        return okrsek;
    }
    
    private EventHandler<TableColumn.CellEditEvent<Kraj, String>> onNazevKrajeEdited = new EventHandler<TableColumn.CellEditEvent<Kraj, String>>() {
        @Override
        public void handle(TableColumn.CellEditEvent<Kraj, String> event) {
            try {
                Kraj kraj = event.getTableView().getItems().get(event.getTablePosition().getRow());
                if(!kraj.getNazev().equals(event.getNewValue())){
                    kraj.setNazev(event.getNewValue());
                    Databaze.upravitKraj(kraj);
                }
            } catch(Exception e){
                Gui.zobrazitVarovani("Chyba", "Vyskytla se chyba při změně hodnoty: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    };
    
    private EventHandler<TableColumn.CellEditEvent<Okrsek, String>> onCisloOkrskuEdited = new EventHandler<TableColumn.CellEditEvent<Okrsek, String>>() {
        @Override
        public void handle(TableColumn.CellEditEvent<Okrsek, String> event) {
            try {
                Okrsek okrsek = event.getTableView().getItems().get(event.getTablePosition().getRow());
                int noveCislo = Integer.parseInt(event.getNewValue());
                if(noveCislo != okrsek.getCislo()){
                    okrsek.setCislo(noveCislo);
                    Databaze.upravitOkrsek(okrsek);
                }
            } catch(NumberFormatException e){
                Gui.zobrazitVarovani("Chyba", "Špatně zadaná číselná hodnota.");
                tableOkrsky.refresh();
            } catch(Exception e){
                Gui.zobrazitVarovani("Chyba", "Vyskytla se chyba při změně hodnoty: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    };
}
