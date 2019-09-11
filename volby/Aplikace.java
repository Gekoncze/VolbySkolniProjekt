package volby;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import volby.css.LokatorCss;


public class Aplikace extends Application {
    private static Stage okno = null;
    
    @Override
    public void start(Stage okno) throws Exception {
        Aplikace.okno = okno;
        zmenaStranky("Prihlaseni.fxml");
        okno.setTitle("Sčítání volebních hlasů");
        okno.setOnCloseRequest(onWindowClosing);
        okno.show();
    }
    
    private final EventHandler<WindowEvent> onWindowClosing = new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
            try {
                if(Databaze.isProvedenaZmena()){
                    int akce = Gui.zobrazitDialogUlozeni();
                    if(akce == Gui.ZPET) { event.consume(); return; }
                    if(akce == Gui.ULOZIT_ZMENY) Databaze.ulozitZmeny();
                    if(akce == Gui.ZAHODIT_ZMENY) Databaze.zahoditZmeny();
                }
                Databaze.odhlasit();
            } catch(Exception e){
                Gui.zobrazitVarovani("Chyba", "Při odhlašování se vyskytla chyba: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    };
    
    public static void zmenaStranky(String nazevStranky){
        Scene scena = new Scene(Gui.nactiStranku(nazevStranky));
        scena.getStylesheets().addAll(LokatorCss.class.getResource("styl_okna.css").toExternalForm());
        okno.setScene(scena);
    }

    public static Stage getOkno() {
        return okno;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
