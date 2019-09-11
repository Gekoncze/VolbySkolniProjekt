package volby.controller;

import com.sun.javafx.beans.event.AbstractNotifyListener;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import volby.Aplikace;
import volby.Databaze;
import volby.Gui;
import volby.MyComboBoxTableCell;
import volby.NevybranZaznamException;
import volby.model.Kandidat;
import volby.model.Kraj;
import volby.model.Strana;


public class SpravaStranController implements Initializable {
    @FXML
    private Label uzivatel;
    
    @FXML
    private TableView<Strana> tableStrany;
    
    @FXML
    private TableView<Kandidat> tableKandidati;
    
    @FXML
    private TableColumn<Strana, String> poradoveCisloStranyColumn;
    
    @FXML
    private TableColumn<Strana, String> zkratkaStranyColumn;
    
    @FXML
    private TableColumn<Strana, String> celyNazevStranyColumn;
    
    @FXML
    private TableColumn<Strana, String> barvaStranyColumn;
    
    @FXML
    private TableColumn<Kandidat, String> poradoveCisloKandidataColumn;
    
    @FXML
    private TableColumn<Kandidat, String> jmenoKandidataColumn;
    
    @FXML
    private TableColumn<Kandidat, String> prijmeniKandidataColumn;
    
    @FXML
    private TableColumn<Kandidat, Kraj> krajKandidataColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            uzivatel.setText(Databaze.getUzivatel().getJmeno() + " " + Databaze.getUzivatel().getPrijmeni());
            
            poradoveCisloStranyColumn.setCellValueFactory(new PropertyValueFactory<>("poradoveCisloText"));
            poradoveCisloStranyColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            poradoveCisloStranyColumn.setOnEditCommit(onPoradoveCisloStranyEdited);
            
            zkratkaStranyColumn.setCellValueFactory(new PropertyValueFactory<>("zkratka"));
            zkratkaStranyColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            zkratkaStranyColumn.setOnEditCommit(onZkratkaStranyEdited);
            
            celyNazevStranyColumn.setCellValueFactory(new PropertyValueFactory<>("celyNazev"));
            celyNazevStranyColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            celyNazevStranyColumn.setOnEditCommit(onCelyNazevStranyEdited);
            
            barvaStranyColumn.setCellValueFactory(new PropertyValueFactory<>("barva"));
            barvaStranyColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            barvaStranyColumn.setOnEditCommit(onBarvaStranyEdited);
            
            poradoveCisloKandidataColumn.setCellValueFactory(new PropertyValueFactory<>("poradoveCisloText"));
            poradoveCisloKandidataColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            poradoveCisloKandidataColumn.setOnEditCommit(onPoradoveCisloKandidataEdited);
            
            jmenoKandidataColumn.setCellValueFactory(new PropertyValueFactory<>("jmeno"));
            jmenoKandidataColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            jmenoKandidataColumn.setOnEditCommit(onJmenoKandidataEdited);
            
            prijmeniKandidataColumn.setCellValueFactory(new PropertyValueFactory<>("prijmeni"));
            prijmeniKandidataColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            prijmeniKandidataColumn.setOnEditCommit(onPrijmeniKandidataEdited);
            
            krajKandidataColumn.setCellValueFactory(new PropertyValueFactory<>("kraj"));
            krajKandidataColumn.setCellFactory(MyComboBoxTableCell.forTableColumn(Databaze.nactiKraje()));
            krajKandidataColumn.setOnEditCommit(onKrajKandidataEdited);
            
            tableStrany.setPlaceholder(new Label("Tabulka neobsahuje data."));
            tableKandidati.setPlaceholder(new Label("Tabulka neobsahuje data."));
            
            aktualizovat();
            
            tableStrany.getSelectionModel().selectedItemProperty().addListener(new AbstractNotifyListener() {
                @Override
                public void invalidated(Observable observable) {
                    try {
                        aktualizovatKandidaty();
                    } catch (Exception e) {
                        Gui.zobrazitVarovani("Chyba", "Při aktualizaci nastala chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                    }
                }
            });
        } catch(Exception e){
            Gui.zobrazitVarovani("Chyba", "Při vytváření stránky se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    private Strana aktualniStrana() throws NevybranZaznamException {
        Strana strana = tableStrany.getSelectionModel().getSelectedItem();
        if(strana == null) throw new NevybranZaznamException();
        return strana;
    }
    
    private Kandidat aktualniKandidat() throws NevybranZaznamException {
        Kandidat kandidat = tableKandidati.getSelectionModel().getSelectedItem();
        if(kandidat == null) throw new NevybranZaznamException();
        return kandidat;
    }
    
    private void aktualizovat() {
        try {
            aktualizovatStrany();
            aktualizovatKandidaty();
        } catch (Exception e) {
            Gui.zobrazitVarovani("Chyba", "Při aktualizaci tabulky se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    private void aktualizovatStrany() throws Exception {
        TableColumn sortcolumn = null;
        SortType st = null;
        if (tableStrany.getSortOrder().size() > 0) {
            sortcolumn = (TableColumn) tableStrany.getSortOrder().get(0);
            st = sortcolumn.getSortType();
        }
        
        int vybranyRadek = 0;
        vybranyRadek = tableStrany.getSelectionModel().getSelectedIndex();
        tableStrany.setItems(Databaze.nactiStranySerazenoPodleCisla());
        
        if (sortcolumn != null) {
            tableStrany.getSortOrder().add(sortcolumn);
            sortcolumn.setSortType(st);
            sortcolumn.setSortable(true);
        }
        
        if(vybranyRadek >= 0) tableStrany.getSelectionModel().select(vybranyRadek);
    }
    
    private void aktualizovatKandidaty() throws Exception {
        try {
            tableKandidati.setItems(Databaze.nactiKandidatySerazenoPodleCisla(aktualniStrana()));
        } catch(NevybranZaznamException e){
            tableKandidati.setItems(null);
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
    private void novaStrana(){
        try {
            Databaze.novaStrana();
            aktualizovat();
        } catch(Exception e){
            Gui.zobrazitVarovani("Chyba", "Při vytváření záznamu se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    @FXML
    private void novyKandidat(){
        try {
            Databaze.novyKandidat(aktualniStrana());
            aktualizovat();
        } catch(Exception e){
            Gui.zobrazitVarovani("Chyba", "Při vytváření záznamu se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    @FXML
    private void odstranitStranu(){
        try {
            Databaze.odstranitStranu(aktualniStrana());
            aktualizovat();
        } catch(NevybranZaznamException e){
            Gui.zobrazitVarovani("Pozor", "Nebyl vybrán žádný záznam.");
        } catch(Exception e){
            Gui.zobrazitVarovani("Chyba", "Při odstraňování záznamu se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    @FXML
    private void odstranitKandidata(){
        try {
            Databaze.odstranitKandidata(aktualniKandidat());
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
    private void zmenitBarvu(){
        try {
            Strana strana = aktualniStrana();
            strana.setBarva(Gui.zobrazitDialogZmenaBarvy(strana.getBarva()));
            Databaze.upravitStranu(strana);
            aktualizovat();
        } catch(NevybranZaznamException e){
            Gui.zobrazitVarovani("Pozor", "Nebyl vybrán žádný záznam!");
        } catch(Exception e){
            Gui.zobrazitVarovani("Chyba", "Při změně hodnoty se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    private EventHandler<TableColumn.CellEditEvent<Strana, String>> onPoradoveCisloStranyEdited = new EventHandler<TableColumn.CellEditEvent<Strana, String>>() {
        @Override
        public void handle(TableColumn.CellEditEvent<Strana, String> event) {
            try {
                Strana strana = event.getTableView().getItems().get(event.getTablePosition().getRow());
                int noveCislo = Integer.parseInt(event.getNewValue());
                if(noveCislo != strana.getPoradoveCislo()){
                    strana.setPoradoveCislo(noveCislo);
                    Databaze.upravitStranu(strana);
                }
            } catch(NumberFormatException e){
                Gui.zobrazitVarovani("Chyba", "Špatně zadaná číselná hodnota.");
                tableStrany.refresh();
            } catch(Exception e){
                Gui.zobrazitVarovani("Chyba", "Vyskytla se chyba při změně hodnoty: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    };
    
    private EventHandler<TableColumn.CellEditEvent<Kandidat, String>> onPoradoveCisloKandidataEdited = new EventHandler<TableColumn.CellEditEvent<Kandidat, String>>() {
        @Override
        public void handle(TableColumn.CellEditEvent<Kandidat, String> event) {
            try {
                Kandidat kandidat = event.getTableView().getItems().get(event.getTablePosition().getRow());
                int noveCislo = Integer.parseInt(event.getNewValue());
                if(noveCislo != kandidat.getPoradoveCislo()){
                    kandidat.setPoradoveCislo(noveCislo);
                    Databaze.upravitKandidata(kandidat);
                }
            } catch(NumberFormatException e){
                Gui.zobrazitVarovani("Chyba", "Špatně zadaná číselná hodnota.");
                tableKandidati.refresh();
            } catch(Exception e){
                Gui.zobrazitVarovani("Chyba", "Vyskytla se chyba při změně hodnoty: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    };
    
    private EventHandler<TableColumn.CellEditEvent<Strana, String>> onZkratkaStranyEdited = new EventHandler<TableColumn.CellEditEvent<Strana, String>>() {
        @Override
        public void handle(TableColumn.CellEditEvent<Strana, String> event) {
            try {
                Strana strana = event.getTableView().getItems().get(event.getTablePosition().getRow());
                if(!strana.getZkratka().equals(event.getNewValue())){
                    strana.setZkratka(event.getNewValue());
                    Databaze.upravitStranu(strana);
                }
            } catch(Exception e){
                Gui.zobrazitVarovani("Chyba", "Vyskytla se chyba při změně hodnoty: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    };
    
    private EventHandler<TableColumn.CellEditEvent<Strana, String>> onCelyNazevStranyEdited = new EventHandler<TableColumn.CellEditEvent<Strana, String>>() {
        @Override
        public void handle(TableColumn.CellEditEvent<Strana, String> event) {
            try {
                Strana strana = event.getTableView().getItems().get(event.getTablePosition().getRow());
                if(!strana.getCelyNazev().equals(event.getNewValue())){
                    strana.setCelyNazev(event.getNewValue());
                    Databaze.upravitStranu(strana);
                }
            } catch(Exception e){
                Gui.zobrazitVarovani("Chyba", "Vyskytla se chyba při změně hodnoty: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    };
    
    private EventHandler<TableColumn.CellEditEvent<Strana, String>> onBarvaStranyEdited = new EventHandler<TableColumn.CellEditEvent<Strana, String>>() {
        @Override
        public void handle(TableColumn.CellEditEvent<Strana, String> event) {
            try {
                Strana strana = event.getTableView().getItems().get(event.getTablePosition().getRow());
                if(!strana.getBarva().equals(event.getNewValue())){
                    strana.setBarva(event.getNewValue());
                    Databaze.upravitStranu(strana);
                }
            } catch(Exception e){
                Gui.zobrazitVarovani("Chyba", "Vyskytla se chyba při změně hodnoty: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    };
    
    private EventHandler<TableColumn.CellEditEvent<Kandidat, String>> onJmenoKandidataEdited = new EventHandler<TableColumn.CellEditEvent<Kandidat, String>>() {
        @Override
        public void handle(TableColumn.CellEditEvent<Kandidat, String> event) {
            try {
                Kandidat kandidat = event.getTableView().getItems().get(event.getTablePosition().getRow());
                if(!kandidat.getJmeno().equals(event.getNewValue())){
                    kandidat.setJmeno(event.getNewValue());
                    Databaze.upravitKandidata(kandidat);
                }
            } catch(Exception e){
                Gui.zobrazitVarovani("Chyba", "Vyskytla se chyba při změně hodnoty: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    };
    
    private EventHandler<TableColumn.CellEditEvent<Kandidat, String>> onPrijmeniKandidataEdited = new EventHandler<TableColumn.CellEditEvent<Kandidat, String>>() {
        @Override
        public void handle(TableColumn.CellEditEvent<Kandidat, String> event) {
            try {
                Kandidat kandidat = event.getTableView().getItems().get(event.getTablePosition().getRow());
                if(!kandidat.getPrijmeni().equals(event.getNewValue())){
                    kandidat.setPrijmeni(event.getNewValue());
                    Databaze.upravitKandidata(kandidat);
                }
            } catch(Exception e){
                Gui.zobrazitVarovani("Chyba", "Vyskytla se chyba při změně hodnoty: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    };
    
    private EventHandler<CellEditEvent<Kandidat, Kraj>> onKrajKandidataEdited = new EventHandler<CellEditEvent<Kandidat, Kraj>>() {
        @Override
        public void handle(CellEditEvent<Kandidat, Kraj> event) {
            try {
                Kandidat kandidat = event.getTableView().getItems().get(event.getTablePosition().getRow());
                if(kandidat.getKrajID() != event.getNewValue().getId()){
                    kandidat.setKrajID(event.getNewValue().getId());
                    Databaze.upravitKandidata(kandidat);
                }
            } catch(Exception e){
                Gui.zobrazitVarovani("Chyba", "Vyskytla se chyba při změně hodnoty: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    };
}
