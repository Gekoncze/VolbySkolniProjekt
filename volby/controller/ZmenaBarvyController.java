package volby.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class ZmenaBarvyController implements Initializable {
    @FXML
    private ColorPicker barva;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
    
    @FXML
    private void potvrdit(){
        ((Stage)barva.getScene().getWindow()).close();
    }

    public String getHodnotaBarvy() {
        return toWebCode(barva.getValue());
    }

    public void setHodnotaBarvy(String hodnotaBarvy) {
        barva.setValue(Color.web(hodnotaBarvy));
    }
    
    private static String toWebCode(Color color){
        return String.format("#%02X%02X%02X",
            (int)(color.getRed() * 255),
            (int)(color.getGreen() * 255),
            (int)(color.getBlue() * 255)
        );
    }
}
