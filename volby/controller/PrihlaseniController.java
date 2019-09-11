package volby.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import volby.Aplikace;
import volby.Databaze;
import volby.Gui;
import volby.Hashovani;
import volby.Role;


public class PrihlaseniController implements Initializable {
    @FXML
    private TextField jmeno;
    
    @FXML
    private PasswordField heslo;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    
    @FXML
    private void prihlasit() {
        try {
            Databaze.prihlasit(jmeno.getText(), Hashovani.vytvoritHash(heslo.getText()));
            Aplikace.zmenaStranky(Role.getNazevVychoziStranky(Databaze.getUzivatel()));
        } catch (Exception e) {
            Gui.zobrazitVarovani("Chyba", "Při přihlašování se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
