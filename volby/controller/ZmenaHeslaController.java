package volby.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import volby.Gui;
import volby.Hashovani;


public class ZmenaHeslaController implements Initializable {
    @FXML
    private PasswordField noveHeslo;
    
    @FXML
    private PasswordField potvrditNoveHeslo;
    
    private String heslo = null;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
    
    @FXML
    private void ulozit(){
        if(noveHeslo.getText().equals(potvrditNoveHeslo.getText())){
            heslo = Hashovani.vytvoritHash(noveHeslo.getText());
            ((Stage)noveHeslo.getScene().getWindow()).close();
        } else {
            Gui.zobrazitVarovani("Pozor", "Hesla se neshodují!");
        }
    }

    public String getHeslo() {
        return heslo;
    }
}
