package volby.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import volby.Gui;


public class UlozeniController implements Initializable {
    private int akce = Gui.ZPET;
    
    @FXML
    private Label popisek;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
    
    @FXML
    private void ulozitZmeny(){
        akce = Gui.ULOZIT_ZMENY;
        ((Stage)popisek.getScene().getWindow()).close();
    }
    
    @FXML
    private void zahoditZmeny(){
        akce = Gui.ZAHODIT_ZMENY;
        ((Stage)popisek.getScene().getWindow()).close();
    }

    public int getAkce() {
        return akce;
    }
}
