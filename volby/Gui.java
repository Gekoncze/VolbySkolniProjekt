package volby;

import volby.controller.ZmenaHeslaController;
import volby.controller.UlozeniController;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import volby.controller.ZmenaBarvyController;
import volby.view.LokatorFormularu;


public class Gui {
    public static final int ZPET = 0;
    public static final int ULOZIT_ZMENY = 1;
    public static final int ZAHODIT_ZMENY = 2;
    
    public static Parent nactiStranku(String nazev) {
        try {
            return FXMLLoader.load(LokatorFormularu.class.getResource(nazev));
        } catch (IOException e) {
            throw new RuntimeException("Nepodarilo se nacist stánku '" + nazev + "': " + e.getMessage());
        }
    }
    
    public static void zobrazitInformaci(String titulek, String zprava){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulek);
        alert.setHeaderText(null);
        alert.setContentText(zprava);
        alert.showAndWait();
    }
    
    public static void zobrazitVarovani(String titulek, String zprava){
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(titulek);
        alert.setHeaderText(null);
        alert.setContentText(zprava);
        alert.showAndWait();
    }
    
    public static Object zobrazitDialog(String nazev, String titulek, INastaveniControlleru nastaveni){
        FXMLLoader fxml = new FXMLLoader();
        Parent formular;
        try {
            formular = fxml.load(LokatorFormularu.class.getResource(nazev).openStream());
        } catch (IOException e) {
            throw new RuntimeException("Nepodařilo se najít dialog '" + nazev + "'");
        }
        Object formularController = fxml.getController();
        if(nastaveni != null) nastaveni.nastavit(formularController);
        
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(Aplikace.getOkno());
        dialog.setScene(new Scene(formular));
        dialog.setTitle(titulek);
        dialog.showAndWait();
        
        return formularController;
    }
    
    public static int zobrazitDialogUlozeni(){
        return ((UlozeniController)zobrazitDialog("Ulozeni.fxml", "Uložit změny?", null)).getAkce();
    }
    
    public static String zobrazitDialogZmenaHesla(){
        return ((ZmenaHeslaController)zobrazitDialog("ZmenaHesla.fxml", "Změna hesla", null)).getHeslo();
    }
    
    public static String zobrazitDialogZmenaBarvy(String vychoziBarva){
        return ((ZmenaBarvyController)zobrazitDialog("ZmenaBarvy.fxml", "Změna barvy", new INastaveniControlleru() {
            @Override
            public void nastavit(Object controller) {
                ((ZmenaBarvyController)controller).setHodnotaBarvy(vychoziBarva);
            }
        })).getHodnotaBarvy();
    }
    
    private static interface INastaveniControlleru {
        public void nastavit(Object controller);
    }
}
