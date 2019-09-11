package volby.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import volby.Aplikace;
import volby.Databaze;
import volby.Gui;


public class MenuSpravaUdajuController implements Initializable {
    @FXML
    private Label uzivatel;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        uzivatel.setText(Databaze.getUzivatel().getJmeno() + " " + Databaze.getUzivatel().getPrijmeni());
    }
    
    @FXML
    private void odhlasit(){
        try {
            Databaze.odhlasit();
            Aplikace.zmenaStranky("Prihlaseni.fxml");
        } catch(Exception e){
            Gui.zobrazitVarovani("Chyba", "Při odhlašování se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    @FXML
    private void spravovatOkrsky(){
        try {
            Aplikace.zmenaStranky("SpravaOkrsku.fxml");
        } catch(Exception e){
            Gui.zobrazitVarovani("Chyba", "Při změně stránky se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    
    @FXML
    private void spravovatStrany(){
        try {
            Aplikace.zmenaStranky("SpravaStran.fxml");
        } catch(Exception e){
            Gui.zobrazitVarovani("Chyba", "Při změně stránky se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
