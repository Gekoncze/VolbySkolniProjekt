package volby;

import volby.model.Uzivatel;


public class Role {
    public static final int SPRAVCE_UZIVATELU = 0;
    public static final int SPRAVCE_UDAJU = 1;
    public static final int ZADAVATEL_HLASU = 2;
    public static final int STATISTIK = 3;
    
    public static final String SPRAVCE_UZIVATELU_TEXT = "Správce uživatelů";
    public static final String SPRAVCE_UDAJU_TEXT = "Správce údajů";
    public static final String ZADAVATEL_HLASU_TEXT = "Zadavatel hlasů";
    public static final String STATISTIK_TEXT = "Statistik";
    
    public static final String[] TEXTY = new String[]{
        SPRAVCE_UZIVATELU_TEXT,
        SPRAVCE_UDAJU_TEXT,
        ZADAVATEL_HLASU_TEXT,
        STATISTIK_TEXT
    };
    
    public static String getNazevVychoziStranky(Uzivatel uzivatel){
        switch(uzivatel.getRole()){
            case Role.SPRAVCE_UZIVATELU: return "SpravaUzivatelu.fxml";
            case Role.SPRAVCE_UDAJU: return "MenuSpravaUdaju.fxml";
            case Role.ZADAVATEL_HLASU: return "SoucetHlasu.fxml";
            case Role.STATISTIK: return "MenuVysledky.fxml";
            default: throw new RuntimeException("Neznámé číslo role " + uzivatel.getRole() + ". Kontaktujte správce uživatelů.");
        }
    }
}
