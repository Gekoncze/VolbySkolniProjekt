package volby.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import volby.Aplikace;
import volby.Databaze;
import volby.NevybranZaznamException;
import volby.Role;
import volby.Gui;
import volby.model.Uzivatel;


public class SpravaUzivateluController implements Initializable {
    @FXML
    private TableView<Uzivatel> table;
    
    @FXML
    private TableColumn<Uzivatel, Integer> idColumn;
    
    @FXML
    private TableColumn<Uzivatel, String> roleColumn;
    
    @FXML
    private TableColumn<Uzivatel, String> pristupoveJmenoColumn;
    
    @FXML
    private TableColumn<Uzivatel, String> jmenoColumn;
    
    @FXML
    private TableColumn<Uzivatel, String> prijmeniColumn;
    
    @FXML
    private TableColumn<Uzivatel, String> telefonColumn;
    
    @FXML
    private Label uzivatel;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            uzivatel.setText(Databaze.getUzivatel().getJmeno() + " " + Databaze.getUzivatel().getPrijmeni());
            
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            
            roleColumn.setCellValueFactory(new PropertyValueFactory<>("roleString"));
            roleColumn.setCellFactory(ComboBoxTableCell.forTableColumn(Role.TEXTY));
            roleColumn.setOnEditCommit(onRoleEdited);
            
            pristupoveJmenoColumn.setCellValueFactory(new PropertyValueFactory<>("pristupoveJmeno"));
            pristupoveJmenoColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            pristupoveJmenoColumn.setOnEditCommit(onPristupoveJmenoEdited);
            
            jmenoColumn.setCellValueFactory(new PropertyValueFactory<>("jmeno"));
            jmenoColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            jmenoColumn.setOnEditCommit(onJmenoEdited);
            
            prijmeniColumn.setCellValueFactory(new PropertyValueFactory<>("prijmeni"));
            prijmeniColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            prijmeniColumn.setOnEditCommit(onPrijmeniEdited);
            
            telefonColumn.setCellValueFactory(new PropertyValueFactory<>("telefon"));
            telefonColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            telefonColumn.setOnEditCommit(onTelefonEdited);
            
            table.setPlaceholder(new Label("Tabulka neobsahuje data."));
            
            aktualizovat();
        } catch(Exception e){
            Gui.zobrazitVarovani("Chyba", "Při vytváření stránky se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    private void aktualizovat() {
        try {
            table.setItems(Databaze.nactiUzivatele());
        } catch (Exception e) {
            Gui.zobrazitVarovani("Chyba", "Při aktualizaci tabulky se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
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
    private void novy(){
        try {
            Databaze.novyUzivatel();
            aktualizovat();
        } catch(Exception e){
            Gui.zobrazitVarovani("Chyba", "Při vytváření záznamu se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    @FXML
    private void odstranit(){
        try {
            Databaze.odstranitUzivatele(aktualniUzivatel());
            aktualizovat();
        } catch(NevybranZaznamException e){
            Gui.zobrazitVarovani("Pozor", "Nebyl vybrán žádný uživatel.");
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
    private void zmenitHeslo(){
        try {
            String noveHeslo = Gui.zobrazitDialogZmenaHesla();
            if(noveHeslo == null) return;
            Databaze.zmenitHesloUzivatele(aktualniUzivatel(), noveHeslo);
        } catch(NevybranZaznamException e){
            Gui.zobrazitVarovani("Pozor", "Nebyl vybrán žádný uživatel.");
        } catch(Exception e){
            Gui.zobrazitVarovani("Chyba", "Při změně hesla se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    private Uzivatel aktualniUzivatel() throws NevybranZaznamException {
        Uzivatel uzivatel = table.getSelectionModel().getSelectedItem();
        if(uzivatel == null) throw new NevybranZaznamException();
        return uzivatel;
    }
    
    private EventHandler<CellEditEvent<Uzivatel, String>> onRoleEdited = new EventHandler<CellEditEvent<Uzivatel, String>>() {
        @Override
        public void handle(CellEditEvent<Uzivatel, String> event) {
            try {
                Uzivatel uzivatel = event.getTableView().getItems().get(event.getTablePosition().getRow());
                if(!uzivatel.getRoleString().equals(event.getNewValue())){
                    uzivatel.setRoleString(event.getNewValue());
                    Databaze.upravitUzivatele(uzivatel);
                }
            } catch(Exception e){
                Gui.zobrazitVarovani("Chyba", "Vyskytla se chyba při změně hodnoty: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    };
    
    private EventHandler<CellEditEvent<Uzivatel, String>> onPristupoveJmenoEdited = new EventHandler<CellEditEvent<Uzivatel, String>>() {
        @Override
        public void handle(CellEditEvent<Uzivatel, String> event) {
            try {
                Uzivatel uzivatel = event.getTableView().getItems().get(event.getTablePosition().getRow());
                if(!uzivatel.getPristupoveJmeno().equals(event.getNewValue())){
                    uzivatel.setPristupoveJmeno(event.getNewValue());
                    Databaze.upravitUzivatele(uzivatel);
                }
            } catch(Exception e){
                Gui.zobrazitVarovani("Chyba", "Vyskytla se chyba při změně hodnoty: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    };
    
    private EventHandler<CellEditEvent<Uzivatel, String>> onJmenoEdited = new EventHandler<CellEditEvent<Uzivatel, String>>() {
        @Override
        public void handle(CellEditEvent<Uzivatel, String> event) {
            try {
                Uzivatel uzivatel = event.getTableView().getItems().get(event.getTablePosition().getRow());
                if(!uzivatel.getJmeno().equals(event.getNewValue())){
                    uzivatel.setJmeno(event.getNewValue());
                    Databaze.upravitUzivatele(uzivatel);
                }
            } catch(Exception e){
                Gui.zobrazitVarovani("Chyba", "Vyskytla se chyba při změně hodnoty: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    };
    
    private EventHandler<CellEditEvent<Uzivatel, String>> onPrijmeniEdited = new EventHandler<CellEditEvent<Uzivatel, String>>() {
        @Override
        public void handle(CellEditEvent<Uzivatel, String> event) {
            try {
                Uzivatel uzivatel = event.getTableView().getItems().get(event.getTablePosition().getRow());
                if(!uzivatel.getPrijmeni().equals(event.getNewValue())){
                    uzivatel.setPrijmeni(event.getNewValue());
                    Databaze.upravitUzivatele(uzivatel);
                }
            } catch(Exception e){
                Gui.zobrazitVarovani("Chyba", "Vyskytla se chyba při změně hodnoty: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    };
    
    private EventHandler<CellEditEvent<Uzivatel, String>> onTelefonEdited = new EventHandler<CellEditEvent<Uzivatel, String>>() {
        @Override
        public void handle(CellEditEvent<Uzivatel, String> event) {
            try {
                Uzivatel uzivatel = event.getTableView().getItems().get(event.getTablePosition().getRow());
                if(!uzivatel.getTelefon().equals(event.getNewValue())){
                    uzivatel.setTelefon(event.getNewValue());
                    Databaze.upravitUzivatele(uzivatel);
                }
            } catch(Exception e){
                Gui.zobrazitVarovani("Chyba", "Vyskytla se chyba při změně hodnoty: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    };
}