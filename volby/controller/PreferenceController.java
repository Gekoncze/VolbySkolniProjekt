package volby.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import volby.Aplikace;
import volby.Databaze;
import volby.Gui;
import volby.NevybranZaznamException;
import volby.model.Kraj;
import volby.model.Preference;
import volby.model.Strana;


public class PreferenceController implements Initializable {
    @FXML
    private Label uzivatel;
    
    @FXML
    private ComboBox<Strana> comboBoxStrana;
    
    @FXML
    private ComboBox<Kraj> comboBoxKraj;
    
    @FXML
    private TableView<Preference> tabulkaPreferenci;
    
    @FXML
    private TableColumn<Preference, String> sloupecJmeno;
    
    @FXML
    private TableColumn<Preference, String> sloupecPrijmeni;
    
    @FXML
    private TableColumn<Preference, Float> sloupecProcenta;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            uzivatel.setText(Databaze.getUzivatel().getJmeno() + " " + Databaze.getUzivatel().getPrijmeni());
            
            tabulkaPreferenci.setPlaceholder(new Label("Tabulka neobsahuje data."));
            
            comboBoxStrana.setPromptText("<vyberte hodnotu>");
            comboBoxKraj.setPromptText("<vyberte hodnotu>");
            comboBoxStrana.setItems(Databaze.nactiStrany());
            comboBoxKraj.setItems(Databaze.nactiKraje());
            
            sloupecJmeno.setCellValueFactory(new PropertyValueFactory<>("jmeno"));
            sloupecPrijmeni.setCellValueFactory(new PropertyValueFactory<>("prijmeni"));
            sloupecProcenta.setCellValueFactory(new PropertyValueFactory<>("procenta"));
            
            aktualizovat();
        } catch(Exception e){
            Gui.zobrazitVarovani("Chyba", "Při vytváření stránky se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    private Strana aktualniStrana() throws NevybranZaznamException {
        Strana strana = comboBoxStrana.getSelectionModel().getSelectedItem();
        if(strana == null) throw new NevybranZaznamException();
        return strana;
    }
    
    private Kraj aktualniKraj() throws NevybranZaznamException {
        Kraj kraj = comboBoxKraj.getSelectionModel().getSelectedItem();
        if(kraj == null) throw new NevybranZaznamException();
        return kraj;
    }
    
    @FXML
    private void zpet(){
        try {
            Aplikace.zmenaStranky("MenuVysledky.fxml");
        } catch(Exception e){
            Gui.zobrazitVarovani("Chyba", "Při odhlašování se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    @FXML
    private void aktualizovat(){
        try {
            tabulkaPreferenci.setItems(Databaze.nactiPreference(aktualniStrana(), aktualniKraj()));
        } catch(NevybranZaznamException e){
            tabulkaPreferenci.setItems(null);
        } catch (Exception e) {
            Gui.zobrazitVarovani("Chyba", "Vyskytl se problém při aktualizaci dat: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    @FXML
    private void zmenaFiltru(){
        aktualizovat();
    }
}
