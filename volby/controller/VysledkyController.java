package volby.controller;

import com.sun.javafx.beans.event.AbstractNotifyListener;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import volby.Aplikace;
import volby.Databaze;
import volby.Gui;
import volby.NevybranZaznamException;
import volby.model.Kraj;
import volby.model.Okrsek;
import volby.model.Vysledek;


public class VysledkyController implements Initializable {
    @FXML
    private Label uzivatel;
    
    @FXML
    private ComboBox<Kraj> comboBoxKraj;
    
    @FXML
    private ComboBox<Okrsek> comboBoxOkrsek;
    
    @FXML
    private BarChart<String, Float> graf;
    
    @FXML
    private CheckBox filtrKraje;
    
    @FXML
    private CheckBox filtrOkrsky;
    
    @FXML
    private CheckBox pouzeNad5Procent;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            uzivatel.setText(Databaze.getUzivatel().getJmeno() + " " + Databaze.getUzivatel().getPrijmeni());
            comboBoxKraj.getSelectionModel().selectedItemProperty().addListener(new AbstractNotifyListener() {
                @Override
                public void invalidated(Observable observable) {
                    try {
                        aktualizovatOkrskyComboBox();
                    } catch (Exception e) {
                        Gui.zobrazitVarovani("Chyba", "Při načítání dat se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                    }
                }
            });
            filtrKraje.selectedProperty().addListener(new AbstractNotifyListener() {
                @Override
                public void invalidated(Observable observable) {
                    zmenaFiltru();
                }
            });
            filtrOkrsky.selectedProperty().addListener(new AbstractNotifyListener() {
                @Override
                public void invalidated(Observable observable) {
                    zmenaFiltru();
                }
            });
            pouzeNad5Procent.selectedProperty().addListener(new AbstractNotifyListener() {
                @Override
                public void invalidated(Observable observable) {
                    zmenaFiltru();
                }
            });
            comboBoxKraj.setPromptText("<vyberte hodnotu>");
            comboBoxOkrsek.setPromptText("<vyberte hodnotu>");
            comboBoxKraj.setItems(Databaze.nactiKraje());
            filtrOkrsky.disableProperty().bind(filtrKraje.selectedProperty().not());
            comboBoxKraj.disableProperty().bind(filtrKraje.selectedProperty().not());
            comboBoxOkrsek.disableProperty().bind(filtrKraje.selectedProperty().not().or(filtrOkrsky.selectedProperty().not()));
            aktualizovat();
        } catch(Exception e){
            Gui.zobrazitVarovani("Chyba", "Při vytváření stránky se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    private Kraj aktualniKraj() throws NevybranZaznamException {
        Kraj kraj = comboBoxKraj.getSelectionModel().getSelectedItem();
        if(kraj == null) throw new NevybranZaznamException();
        return kraj;
    }
    
    private Okrsek aktualniOkrsek() throws NevybranZaznamException {
        Okrsek okrsek = comboBoxOkrsek.getSelectionModel().getSelectedItem();
        if(okrsek == null) throw new NevybranZaznamException();
        return okrsek;
    }
    
    private void aktualizovatOkrskyComboBox() throws Exception {
        try {
            comboBoxOkrsek.setItems(Databaze.nactiOkrsky(aktualniKraj()));
        } catch(NevybranZaznamException e){
            comboBoxOkrsek.setItems(null);
        }
    }
    
    @FXML
    private void zpet(){
        try {
            Aplikace.zmenaStranky("MenuVysledky.fxml");
        } catch(Exception e){
            Gui.zobrazitVarovani("Chyba", "Při odhlašování se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    private static boolean flip = false;
    private String doFlip(){
        if(flip){
            flip = false;
            return "";
        } else {
            flip = true;
            return " ";
        }
    }
    
    @FXML
    private void aktualizovat(){
        String flipString = doFlip();
        
        try {
            LinkedList<Vysledek> vysledky;
            if(filtrKraje.isSelected()){
                if(filtrOkrsky.isSelected()){
                    try {
                        vysledky = Databaze.nactiVysledky(pouzeNad5Procent.selectedProperty().getValue() ? 5 : 0, aktualniOkrsek());
                    } catch(NevybranZaznamException e){
                        vysledky = Databaze.nactiVysledky(pouzeNad5Procent.selectedProperty().getValue() ? 5 : 0, (Okrsek)null);
                    }
                } else {
                    try {
                        vysledky = Databaze.nactiVysledky(pouzeNad5Procent.selectedProperty().getValue() ? 5 : 0, aktualniKraj());
                    } catch(NevybranZaznamException e){
                        vysledky = Databaze.nactiVysledky(pouzeNad5Procent.selectedProperty().getValue() ? 5 : 0, (Kraj)null);
                    }
                }
            } else {
                vysledky = Databaze.nactiVysledky(pouzeNad5Procent.selectedProperty().getValue() ? 5 : 0);
            }
            
            ObservableList<XYChart.Series<String, Float>> grafyData = FXCollections.observableArrayList();
            Series<String, Float> grafData = new Series<>();
            ObservableList<XYChart.Data<String, Float>> data = FXCollections.observableArrayList();
            for(Vysledek vysledek : vysledky){
                data.add(new XYChart.Data<>(vysledek.getZkratka() + flipString, vysledek.getProcenta()));
            }
            grafData.dataProperty().set(data);
            grafyData.add(grafData);
            graf.dataProperty().setValue(grafyData);
            int i = 0;
            for(Vysledek vysledek : vysledky){
                graf.lookup(".data" + i + ".chart-bar").setStyle("-fx-bar-fill: " + vysledek.getBarva());
                i++;
            }
        } catch (Exception e) {
            Gui.zobrazitVarovani("Chyba", "Vyskytl se problém při aktualizaci dat: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    @FXML
    private void zmenaFiltru(){
        aktualizovat();
    }
}
