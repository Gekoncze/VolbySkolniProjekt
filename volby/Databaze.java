package volby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import volby.model.Hlas;
import volby.model.Kandidat;
import volby.model.Kraj;
import volby.model.Okrsek;
import volby.model.Preference;
import volby.model.PreferencniHlas;
import volby.model.Strana;
import volby.model.Uzivatel;
import volby.model.Vysledek;


public class Databaze {
    private static Connection pripojeni = null;
    private static Uzivatel uzivatel = null;
    private static boolean provedenaZmena = false;
    private static boolean vypisPrikazuDoKonzole = false;
    
    public static void prihlasit(String pristupoveJmeno, String pristupoveHeslo) throws Exception {
        pripojit();
        PreparedStatement prihlaseni = pripojeni.prepareCall(
                "SELECT id, role, pristupove_jmeno, jmeno, prijmeni, telefon FROM uzivatele " + 
                "WHERE pristupove_jmeno = ? AND pristupove_heslo = ?"
        );
        prihlaseni.setString(1, pristupoveJmeno);
        prihlaseni.setString(2, pristupoveHeslo);
        ResultSet vysledekDotazu = prihlaseni.executeQuery();
        if(vysledekDotazu.next()){
            uzivatel = new Uzivatel();
            uzivatel.setId(vysledekDotazu.getInt("id"));
            uzivatel.setRole(vysledekDotazu.getInt("role"));
            uzivatel.setPristupoveJmeno(vysledekDotazu.getString("pristupove_jmeno"));
            uzivatel.setJmeno(vysledekDotazu.getString("jmeno"));
            uzivatel.setPrijmeni(vysledekDotazu.getString("prijmeni"));
            uzivatel.setTelefon(vysledekDotazu.getString("telefon"));
        } else {
            odhlasit();
            throw new PrihlaseniException();
        }
    }
    
    private static void pripojit() throws Exception {
        if(pripojeni != null && !pripojeni.isClosed()) pripojeni.close();
        Class.forName("oracle.jdbc.OracleDriver");
        pripojeni = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "admin", "admin");
        pripojeni.setAutoCommit(false);
    }
    
    public static void odhlasit() throws Exception {
        if(pripojeni != null) pripojeni.close();
        pripojeni = null;
        uzivatel = null;
        provedenaZmena = false;
    }
    
    public static void ulozitZmeny() throws Exception {
        pripojeni.commit();
        provedenaZmena = false;
    }
    
    public static void zahoditZmeny() throws Exception {
        pripojeni.rollback();
        provedenaZmena = false;
    }

    public static Uzivatel getUzivatel() {
        return uzivatel;
    }

    public static boolean isProvedenaZmena() {
        return provedenaZmena;
    }

    public static void setVypisPrikazuDoKonzole(boolean vypisPrikazuDoKonzole) {
        Databaze.vypisPrikazuDoKonzole = vypisPrikazuDoKonzole;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // METODY PRO NAČÍTÁNÍ JEDNOTLIVÝCH ZÁZNAMŮ                               //
    ////////////////////////////////////////////////////////////////////////////
    
    public static Kraj nactiKraj(int id) throws Exception {
        PreparedStatement dotaz = pripojeni.prepareCall(
                "SELECT nazev FROM kraje WHERE id = ?"
        );
        dotaz.setInt(1, id);
        ResultSet data = dotaz.executeQuery();
        try {
            if (data.next()) {
                Kraj kraj = new Kraj();
                kraj.setId(id);
                kraj.setNazev(data.getString("nazev"));
                return kraj;
            } else {
                throw new Exception("Záznam nenalezen.");
            }
        } finally {
            data.close();
            dotaz.close();
        }
    }
    
    public static Strana nactiStranu(int id) throws Exception {
        PreparedStatement dotaz = pripojeni.prepareCall(
                "SELECT zkratka, cely_nazev, poradove_cislo, barva FROM strany WHERE id = ?"
        );
        dotaz.setInt(1, id);
        ResultSet data = dotaz.executeQuery();
        try {
            if (data.next()) {
                Strana strana = new Strana();
                strana.setId(id);
                strana.setZkratka(data.getString("zkratka"));
                strana.setCelyNazev(data.getString("cely_nazev"));
                strana.setPoradoveCislo(data.getInt("poradove_cislo"));
                strana.setBarva(data.getString("barva"));
                return strana;
            } else {
                throw new Exception("Záznam nenalezen.");
            }
        } finally {
            data.close();
            dotaz.close();
        }
    }
    
    public static Kandidat nactiKandidata(int id) throws Exception {
        PreparedStatement dotaz = pripojeni.prepareCall(
                "SELECT jmeno, prijmeni, poradove_cislo, kraj_id, strana_id FROM kandidati WHERE id = ?"
        );
        dotaz.setInt(1, id);
        ResultSet data = dotaz.executeQuery();
        try {
            if (data.next()) {
                Kandidat kandidat = new Kandidat();
                kandidat.setId(id);
                kandidat.setJmeno(data.getString("jmeno"));
                kandidat.setPrijmeni(data.getString("prijmeni"));
                kandidat.setStranaID(data.getInt("strana_id"));
                kandidat.setPoradoveCislo(data.getInt("poradove_cislo"));
                kandidat.setKrajID(data.getInt("kraj_id"));
                return kandidat;
            } else {
                throw new Exception("Záznam nenalezen.");
            }
        } finally {
            data.close();
            dotaz.close();
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // METODY PRO NAČÍTÁNÍ VICE DAT Z TABULEK                                 //
    ////////////////////////////////////////////////////////////////////////////
    
    public static ObservableList<Uzivatel> nactiUzivatele() throws Exception {
        ObservableList<Uzivatel> uzivatele = FXCollections.observableArrayList();
        PreparedStatement dotaz = pripojeni.prepareCall(
                "SELECT id, role, pristupove_jmeno, jmeno, prijmeni, telefon FROM uzivatele ORDER BY id ASC"
        );
        ResultSet data = dotaz.executeQuery();
        try {
            while (data.next()) {
                Uzivatel uzivatel = new Uzivatel();
                uzivatel.setId(data.getInt("id"));
                uzivatel.setPristupoveJmeno(data.getString("pristupove_jmeno"));
                uzivatel.setJmeno(data.getString("jmeno"));
                uzivatel.setPrijmeni(data.getString("prijmeni"));
                uzivatel.setTelefon(data.getString("telefon"));
                uzivatel.setRole(data.getInt("role"));
                uzivatele.add(uzivatel);
            }
            return uzivatele;
        } finally {
            data.close();
            dotaz.close();
        }
    }
    
    public static ObservableList<Kraj> nactiKraje() throws Exception {
        ObservableList<Kraj> kraje = FXCollections.observableArrayList();
        PreparedStatement dotaz = pripojeni.prepareCall(
                "SELECT id, nazev FROM kraje ORDER BY nazev ASC"
        );
        ResultSet data = dotaz.executeQuery();
        try {
            while (data.next()) {
                Kraj kraj = new Kraj();
                kraj.setId(data.getInt("id"));
                kraj.setNazev(data.getString("nazev"));
                kraje.add(kraj);
            }
            return kraje;
        } finally {
            data.close();
            dotaz.close();
        }
    }
    
    public static ObservableList<Okrsek> nactiOkrsky(Kraj kraj) throws Exception {
        if(kraj == null) kraj = new Kraj();
        ObservableList<Okrsek> okrsky = FXCollections.observableArrayList();
        PreparedStatement dotaz = pripojeni.prepareCall(
                "SELECT id, cislo FROM okrsky WHERE kraj_id = ? ORDER BY cislo ASC"
        );
        dotaz.setInt(1, kraj.getId());
        ResultSet data = dotaz.executeQuery();
        try {
            while (data.next()) {
                Okrsek okrsek = new Okrsek();
                okrsek.setId(data.getInt("id"));
                okrsek.setCislo(data.getInt("cislo"));
                okrsek.setKrajID(kraj.getId());
                okrsky.add(okrsek);
            }
            return okrsky;
        } finally {
            data.close();
            dotaz.close();
        }
    }
    
    public static ObservableList<Strana> nactiStrany() throws Exception {
        ObservableList<Strana> strany = FXCollections.observableArrayList();
        PreparedStatement dotaz = pripojeni.prepareCall(
                "SELECT id, zkratka, cely_nazev, poradove_cislo, barva FROM strany ORDER BY zkratka ASC"
        );
        ResultSet data = dotaz.executeQuery();
        try {
            while (data.next()) {
                Strana strana = new Strana();
                strana.setId(data.getInt("id"));
                strana.setZkratka(data.getString("zkratka"));
                strana.setCelyNazev(data.getString("cely_nazev"));
                strana.setPoradoveCislo(data.getInt("poradove_cislo"));
                strana.setBarva(data.getString("barva"));
                strany.add(strana);
            }
            return strany;
        } finally {
            data.close();
            dotaz.close();
        }
    }
    
    public static ObservableList<Strana> nactiStranySerazenoPodleCisla() throws Exception {
        ObservableList<Strana> strany = FXCollections.observableArrayList();
        PreparedStatement dotaz = pripojeni.prepareCall(
                "SELECT id, zkratka, cely_nazev, poradove_cislo, barva FROM strany ORDER BY poradove_cislo ASC"
        );
        ResultSet data = dotaz.executeQuery();
        try {
            while (data.next()) {
                Strana strana = new Strana();
                strana.setId(data.getInt("id"));
                strana.setZkratka(data.getString("zkratka"));
                strana.setCelyNazev(data.getString("cely_nazev"));
                strana.setPoradoveCislo(data.getInt("poradove_cislo"));
                strana.setBarva(data.getString("barva"));
                strany.add(strana);
            }
            return strany;
        } finally {
            data.close();
            dotaz.close();
        }
    }
    
    public static ObservableList<Kandidat> nactiKandidaty(Strana strana) throws Exception {
        if(strana == null) strana = new Strana();
        ObservableList<Kandidat> kandidati = FXCollections.observableArrayList();
        PreparedStatement dotaz = pripojeni.prepareCall(
                "SELECT id, jmeno, prijmeni, poradove_cislo, kraj_id FROM kandidati " +
                "WHERE strana_id = ? ORDER BY jmeno ASC, prijmeni ASC"
        );
        dotaz.setInt(1, strana.getId());
        ResultSet data = dotaz.executeQuery();
        try {
            while(data.next()){
                Kandidat kandidat = new Kandidat();
                kandidat.setId(data.getInt("id"));
                kandidat.setJmeno(data.getString("jmeno"));
                kandidat.setPrijmeni(data.getString("prijmeni"));
                kandidat.setStranaID(strana.getId());
                kandidat.setPoradoveCislo(data.getInt("poradove_cislo"));
                kandidat.setKrajID(data.getInt("kraj_id"));
                kandidati.add(kandidat);
            }
            return kandidati;
        } finally {
            data.close();
            dotaz.close();
        }
    }
    
    public static ObservableList<Kandidat> nactiKandidatySerazenoPodleCisla(Strana strana) throws Exception {
        if(strana == null) strana = new Strana();
        ObservableList<Kandidat> kandidati = FXCollections.observableArrayList();
        PreparedStatement dotaz = pripojeni.prepareCall(
                "SELECT id, jmeno, prijmeni, poradove_cislo, kraj_id FROM kandidati " +
                "WHERE strana_id = ? ORDER BY poradove_cislo ASC"
        );
        dotaz.setInt(1, strana.getId());
        ResultSet data = dotaz.executeQuery();
        try {
            while(data.next()){
                Kandidat kandidat = new Kandidat();
                kandidat.setId(data.getInt("id"));
                kandidat.setJmeno(data.getString("jmeno"));
                kandidat.setPrijmeni(data.getString("prijmeni"));
                kandidat.setStranaID(strana.getId());
                kandidat.setPoradoveCislo(data.getInt("poradove_cislo"));
                kandidat.setKrajID(data.getInt("kraj_id"));
                kandidati.add(kandidat);
            }
            return kandidati;
        } finally {
            data.close();
            dotaz.close();
        }
    }
    
    public static ObservableList<Kandidat> nactiKandidaty(Strana strana, Kraj kraj) throws Exception {
        if(strana == null) strana = new Strana();
        if(kraj == null) kraj = new Kraj();
        ObservableList<Kandidat> kandidati = FXCollections.observableArrayList();
        PreparedStatement dotaz = pripojeni.prepareCall(
                "SELECT id, jmeno, prijmeni, poradove_cislo FROM kandidati " +
                "WHERE strana_id = ? AND kraj_id = ? ORDER BY jmeno ASC, prijmeni ASC"
        );
        dotaz.setInt(1, strana.getId());
        dotaz.setInt(2, kraj.getId());
        ResultSet data = dotaz.executeQuery();
        try {
            while (data.next()) {
                Kandidat kandidat = new Kandidat();
                kandidat.setId(data.getInt("id"));
                kandidat.setJmeno(data.getString("jmeno"));
                kandidat.setPrijmeni(data.getString("prijmeni"));
                kandidat.setStranaID(strana.getId());
                kandidat.setPoradoveCislo(data.getInt("poradove_cislo"));
                kandidat.setKrajID(kraj.getId());
                kandidati.add(kandidat);
            }
            return kandidati;
        } finally {
            data.close();
            dotaz.close();
        }
    }
    
    public static ObservableList<Hlas> nactiHlasy(Okrsek okrsek) throws Exception {
        if(okrsek == null) okrsek = new Okrsek();
        ObservableList<Hlas> hlasy = FXCollections.observableArrayList();
        PreparedStatement dotaz = pripojeni.prepareCall(
                "SELECT id, strana_id, cas_vytvoreni FROM hlasy WHERE uzivatel_id = ? AND okrsek_id = ?"
        );
        dotaz.setInt(1, uzivatel.getId());
        dotaz.setInt(2, okrsek.getId());
        ResultSet data = dotaz.executeQuery();
        try {
            while (data.next()) {
                Hlas hlas = new Hlas();
                hlas.setId(data.getInt("id"));
                hlas.setStranaID(data.getInt("strana_id"));
                hlas.setCasVytvoreni(data.getTimestamp("cas_vytvoreni"));
                hlas.setUzivatelID(uzivatel.getId());
                hlas.setOkrsekID(okrsek.getId());
                hlasy.add(hlas);
            }
            return hlasy;
        } finally {
            data.close();
            dotaz.close();
        }
    }
    
    public static ObservableList<PreferencniHlas> nactiPreferencniHlasy(Hlas hlas) throws Exception {
        if(hlas == null) hlas = new Hlas();
        ObservableList<PreferencniHlas> phlasy = FXCollections.observableArrayList();
        PreparedStatement dotaz = pripojeni.prepareCall(
                "SELECT id, kandidat_id FROM preferencni_hlasy WHERE hlas_id = ?"
        );
        dotaz.setInt(1, hlas.getId());
        ResultSet data = dotaz.executeQuery();
        try {
            while (data.next()) {
                PreferencniHlas phlas = new PreferencniHlas();
                phlas.setId(data.getInt("id"));
                phlas.setHlasID(hlas.getId());
                phlas.setKandidatID(data.getInt("kandidat_id"));
                phlasy.add(phlas);
            }
            return phlasy;
        } finally {
            data.close();
            dotaz.close();
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // METODY PRO VKLÁDÁNÍ NOVÝCH ZÁZNAMŮ                                     //
    ////////////////////////////////////////////////////////////////////////////
    
    public static void novyUzivatel() throws Exception {
        String textDotatu;
        provedenaZmena = true;
        PreparedStatement dotaz = pripojeni.prepareCall( textDotatu = 
                "INSERT INTO uzivatele (id, role, pristupove_jmeno, pristupove_heslo, jmeno, prijmeni, telefon) " +
                "VALUES (uzivatele_seq.nextval, -1, '<nezadano>', '<nezadano>', '<nezadano>', '<nezadano>', '<nezadano>')"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Chyba vložení záznamu do databáze.");
        }
        dotaz.close();
    }
    
    public static void novyKraj() throws Exception {
        String textDotatu;
        provedenaZmena = true;
        PreparedStatement dotaz = pripojeni.prepareCall( textDotatu = 
                "INSERT INTO kraje (id, nazev) VALUES (kraje_seq.nextval, '<nezadano>')"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Chyba vložení záznamu do databáze.");
        }
        dotaz.close();
    }
    
    public static void novyOkrsek(Kraj kraj) throws Exception {
        String textDotatu;
        provedenaZmena = true;
        PreparedStatement dotaz = pripojeni.prepareCall( textDotatu = 
                "INSERT INTO okrsky (id, cislo, kraj_id) VALUES (okrsky_seq.nextval, 0, ?)"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        dotaz.setInt(1, kraj.getId());
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Chyba vložení záznamu do databáze.");
        }
        dotaz.close();
    }
    
    public static void novaStrana() throws Exception {
        String textDotatu;
        provedenaZmena = true;
        PreparedStatement dotaz = pripojeni.prepareCall( textDotatu = 
                "INSERT INTO strany (id, zkratka, cely_nazev, poradove_cislo, barva) " +
                "VALUES (strany_seq.nextval, '<nezadano>', '<nezadano>', 0, '#000000')"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Chyba vložení záznamu do databáze.");
        }
        dotaz.close();
    }
    
    public static void novyKandidat(Strana strana) throws Exception {
        String textDotatu;
        provedenaZmena = true;
        PreparedStatement dotaz = pripojeni.prepareCall( textDotatu = 
                "INSERT INTO kandidati (id, jmeno, prijmeni, poradove_cislo, strana_id, kraj_id) " +
                "VALUES (kandidati_seq.nextval, '<nezadano>', '<nezadano>', 0, ?, -1)"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        dotaz.setInt(1, strana.getId());
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Chyba vložení záznamu do databáze.");
        }
        dotaz.close();
    }
    
    public static void novyHlas(Okrsek okrsek) throws Exception {
        String textDotatu;
        provedenaZmena = true;
        PreparedStatement dotaz = pripojeni.prepareCall( textDotatu = 
                "INSERT INTO hlasy (id, strana_id, uzivatel_id, okrsek_id, cas_vytvoreni) " +
                "VALUES (hlasy_seq.nextval, 0, ?, ?, SYSTIMESTAMP)"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        dotaz.setInt(1, uzivatel.getId());
        dotaz.setInt(2, okrsek.getId());
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Chyba vložení záznamu do databáze.");
        }
        dotaz.close();
    }
    
    public static void novyPreferencniHlas(Hlas hlas) throws Exception {
        String textDotatu;
        provedenaZmena = true;
        PreparedStatement dotaz = pripojeni.prepareCall( textDotatu = 
                "INSERT INTO preferencni_hlasy (id, hlas_id, kandidat_id) " +
                "VALUES (preferencni_hlasy_seq.nextval, ?, 0)"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        dotaz.setInt(1, hlas.getId());
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Chyba vložení záznamu do databáze.");
        }
        dotaz.close();
    }
    
    public static void novyUzivatel(Uzivatel uzivatel) throws Exception {
        String textDotatu;
        provedenaZmena = true;
        PreparedStatement dotaz = pripojeni.prepareCall(
                "INSERT INTO uzivatele (id, role, pristupove_jmeno, pristupove_heslo, jmeno, prijmeni, telefon) " +
                "VALUES (uzivatele_seq.nextval, ?, ?, ?, ?, ?, ?)"
        );
        textDotatu = 
                "INSERT INTO uzivatele (id, role, pristupove_jmeno, pristupove_heslo, jmeno, prijmeni, telefon) " +
                "VALUES (uzivatele_seq.nextval, " + uzivatel.getRole() + ", '" + uzivatel.getPristupoveJmeno() + "', '" + uzivatel.getPristupoveHeslo() + "', '" + uzivatel.getJmeno() + "', '" + uzivatel.getPrijmeni() + "', '" + uzivatel.getTelefon() + "');";
        dotaz.setInt(1, uzivatel.getRole());
        dotaz.setString(2, uzivatel.getPristupoveJmeno());
        dotaz.setString(3, uzivatel.getPristupoveHeslo());
        dotaz.setString(4, uzivatel.getJmeno());
        dotaz.setString(5, uzivatel.getPrijmeni());
        dotaz.setString(6, uzivatel.getTelefon());
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Chyba vložení záznamu do databáze.");
        }
        dotaz.close();
    }
    
    public static void novyKraj(Kraj kraj) throws Exception {
        String textDotatu;
        provedenaZmena = true;
        PreparedStatement dotaz = pripojeni.prepareCall(
                "INSERT INTO kraje (id, nazev) VALUES (kraje_seq.nextval, ?)"
        );
        textDotatu = "INSERT INTO kraje (id, nazev) VALUES (kraje_seq.nextval, '" + kraj.getNazev() + "');";
        dotaz.setString(1, kraj.getNazev());
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Chyba vložení záznamu do databáze.");
        }
        dotaz.close();
    }
    
    public static void novyOkrsek(Okrsek okrsek) throws Exception {
        String textDotatu;
        provedenaZmena = true;
        PreparedStatement dotaz = pripojeni.prepareCall(
                "INSERT INTO okrsky (id, cislo, kraj_id) VALUES (okrsky_seq.nextval, ?, ?)"
        );
        textDotatu = "INSERT INTO okrsky (id, cislo, kraj_id) VALUES (okrsky_seq.nextval, " + okrsek.getCislo() + ", " + okrsek.getKrajID() + ");";
        dotaz.setInt(1, okrsek.getCislo());
        dotaz.setInt(2, okrsek.getKrajID());
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Chyba vložení záznamu do databáze.");
        }
        dotaz.close();
    }
    
    public static void novaStrana(Strana strana) throws Exception {
        String textDotatu;
        provedenaZmena = true;
        PreparedStatement dotaz = pripojeni.prepareCall(
                "INSERT INTO strany (id, zkratka, cely_nazev, poradove_cislo, barva) " +
                "VALUES (strany_seq.nextval, ?, ?, ?, ?)"
        );
        textDotatu = 
                "INSERT INTO strany (id, zkratka, cely_nazev, poradove_cislo, barva) " +
                "VALUES (strany_seq.nextval, '" + strana.getZkratka() + "', '" + strana.getCelyNazev() + "', " + strana.getPoradoveCislo() + ", '" + strana.getBarva() + "');";
        dotaz.setString(1, strana.getZkratka());
        dotaz.setString(2, strana.getCelyNazev());
        dotaz.setInt(3, strana.getPoradoveCislo());
        dotaz.setString(4, strana.getBarva());
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Chyba vložení záznamu do databáze.");
        }
        dotaz.close();
    }
    
    public static void novyKandidat(Kandidat kandidat) throws Exception {
        String textDotatu;
        provedenaZmena = true;
        PreparedStatement dotaz = pripojeni.prepareCall(
                "INSERT INTO kandidati (id, jmeno, prijmeni, poradove_cislo, strana_id, kraj_id) " +
                "VALUES (kandidati_seq.nextval, ?, ?, ?, ?, ?)"
        );
        textDotatu = 
                "INSERT INTO kandidati (id, jmeno, prijmeni, poradove_cislo, strana_id, kraj_id) " +
                "VALUES (kandidati_seq.nextval, '" + kandidat.getJmeno() + "', '" + kandidat.getPrijmeni() + "', " + kandidat.getPoradoveCislo() + ", " + kandidat.getStranaID() + ", " + kandidat.getKrajID() + ");";
        dotaz.setString(1, kandidat.getJmeno());
        dotaz.setString(2, kandidat.getPrijmeni());
        dotaz.setInt(3, kandidat.getPoradoveCislo());
        dotaz.setInt(4, kandidat.getStranaID());
        dotaz.setInt(5, kandidat.getKrajID());
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Chyba vložení záznamu do databáze.");
        }
        dotaz.close();
    }
    
    public static void novyHlas(Hlas hlas) throws Exception {
        String textDotatu;
        provedenaZmena = true;
        PreparedStatement dotaz = pripojeni.prepareCall(
                "INSERT INTO hlasy (id, strana_id, uzivatel_id, okrsek_id, cas_vytvoreni) " +
                "VALUES (hlasy_seq.nextval, ?, ?, ?, SYSTIMESTAMP)"
        );
        textDotatu = 
                "INSERT INTO hlasy (id, strana_id, uzivatel_id, okrsek_id, cas_vytvoreni) " +
                "VALUES (hlasy_seq.nextval, " + hlas.getStranaID() + ", " + hlas.getUzivatelID() + ", " + hlas.getOkrsekID() + ", SYSTIMESTAMP);";
        dotaz.setInt(1, hlas.getStranaID());
        dotaz.setInt(2, hlas.getUzivatelID());
        dotaz.setInt(3, hlas.getOkrsekID());
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Chyba vložení záznamu do databáze.");
        }
        dotaz.close();
    }
    
    public static void novyPreferencniHlas(PreferencniHlas hlas) throws Exception {
        String textDotatu;
        provedenaZmena = true;
        PreparedStatement dotaz = pripojeni.prepareCall(
                "INSERT INTO preferencni_hlasy (id, hlas_id, kandidat_id) " +
                "VALUES (preferencni_hlasy_seq.nextval, ?, ?)"
        );
        textDotatu = 
                "INSERT INTO preferencni_hlasy (id, hlas_id, kandidat_id) " +
                "VALUES (preferencni_hlasy_seq.nextval, " + hlas.getHlasID() + ", " + hlas.getKandidatID() + ");";
        dotaz.setInt(1, hlas.getHlasID());
        dotaz.setInt(2, hlas.getKandidatID());
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Chyba vložení záznamu do databáze.");
        }
        dotaz.close();
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // METODY PRO ODSTRAŇOVÁNÍ ZÁZNAMŮ                                        //
    ////////////////////////////////////////////////////////////////////////////
    
    public static void odstranitUzivatele(Uzivatel uzivatel) throws Exception {
        String textDotatu;
        provedenaZmena = true;
        PreparedStatement dotaz = pripojeni.prepareCall( textDotatu = 
                "DELETE FROM uzivatele WHERE id = ?"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        dotaz.setInt(1, uzivatel.getId());
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Nepodařilo se najít záznam uživatele.");
        }
        dotaz.close();
    }
    
    public static void odstranitKraj(Kraj kraj) throws Exception {
        String textDotatu;
        provedenaZmena = true;
        
        PreparedStatement dotaz = pripojeni.prepareCall( textDotatu = 
                "DELETE FROM okrsky WHERE kraj_id = ?"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        dotaz.setInt(1, kraj.getId());
        dotaz.executeUpdate();
        
        dotaz = pripojeni.prepareCall( textDotatu = 
                "DELETE FROM kraje WHERE id = ?"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        dotaz.setInt(1, kraj.getId());
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Nepodařilo se najít záznam.");
        }
        dotaz.close();
    }
    
    public static void odstranitOkrsek(Okrsek okrsek) throws Exception {
        String textDotatu;
        provedenaZmena = true;
        PreparedStatement dotaz = pripojeni.prepareCall( textDotatu = 
                "DELETE FROM okrsky WHERE id = ?"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        dotaz.setInt(1, okrsek.getId());
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Nepodařilo se najít záznam.");
        }
        dotaz.close();
    }
    
    public static void odstranitStranu(Strana strana) throws Exception {
        String textDotatu;
        provedenaZmena = true;
        PreparedStatement dotaz = pripojeni.prepareCall( textDotatu = 
                "DELETE FROM kandidati WHERE strana_id = ?"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        dotaz.setInt(1, strana.getId());
        dotaz.executeUpdate();
        
        dotaz = pripojeni.prepareCall( textDotatu = 
                "DELETE FROM strany WHERE id = ?"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        dotaz.setInt(1, strana.getId());
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Nepodařilo se najít záznam.");
        }
        dotaz.close();
    }
    
    public static void odstranitKandidata(Kandidat kandidat) throws Exception {
        String textDotatu;
        provedenaZmena = true;
        PreparedStatement dotaz = pripojeni.prepareCall( textDotatu = 
                "DELETE FROM kandidati WHERE id = ?"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        dotaz.setInt(1, kandidat.getId());
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Nepodařilo se najít záznam.");
        }
        dotaz.close();
    }
    
    public static void odstranitHlas(Hlas hlas) throws Exception {
        String textDotatu;
        provedenaZmena = true;
        PreparedStatement dotaz = pripojeni.prepareCall( textDotatu = 
                "DELETE FROM preferencni_hlasy WHERE hlas_id = ?"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        dotaz.setInt(1, hlas.getId());
        dotaz.executeUpdate();
        
        dotaz = pripojeni.prepareCall( textDotatu = 
                "DELETE FROM hlasy WHERE id = ?"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        dotaz.setInt(1, hlas.getId());
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Nepodařilo se najít záznam.");
        }
        dotaz.close();
    }
    
    public static void odstranitPreferencniHlas(PreferencniHlas preferencniHlas) throws Exception {
        String textDotatu;
        provedenaZmena = true;
        PreparedStatement dotaz = pripojeni.prepareCall( textDotatu = 
                "DELETE FROM preferencni_hlasy WHERE id = ?"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        dotaz.setInt(1, preferencniHlas.getId());
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Nepodařilo se najít záznam.");
        }
        dotaz.close();
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // METODY PRO ÚPRAVU ZÁZNAMŮ                                              //
    ////////////////////////////////////////////////////////////////////////////
    
    public static void upravitUzivatele(Uzivatel uzivatel) throws Exception {
        String textDotatu;
        provedenaZmena = true;
        PreparedStatement dotaz = pripojeni.prepareCall( textDotatu = 
                "UPDATE uzivatele SET role = ?, pristupove_jmeno = ?, jmeno = ?, prijmeni = ?, telefon = ? WHERE id = ?"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        dotaz.setInt(1, uzivatel.getRole());
        dotaz.setString(2, uzivatel.getPristupoveJmeno());
        dotaz.setString(3, uzivatel.getJmeno());
        dotaz.setString(4, uzivatel.getPrijmeni());
        dotaz.setString(5, uzivatel.getTelefon());
        dotaz.setInt(6, uzivatel.getId());
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Nepodařilo se najít záznam.");
        }
        dotaz.close();
    }
    
    public static void zmenitHesloUzivatele(Uzivatel uzivatel, String noveHeslo) throws Exception {
        String textDotatu;
        provedenaZmena = true;
        PreparedStatement dotaz = pripojeni.prepareCall( textDotatu = 
                "UPDATE uzivatele SET pristupove_heslo = ? WHERE id = ?"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        dotaz.setString(1, noveHeslo);
        dotaz.setInt(2, uzivatel.getId());
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Nepodařilo se najít.");
        }
        dotaz.close();
    }
    
    public static void upravitKraj(Kraj kraj) throws Exception {
        String textDotatu;
        provedenaZmena = true;
        PreparedStatement dotaz = pripojeni.prepareCall( textDotatu = 
                "UPDATE kraje SET nazev = ? WHERE id = ?"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        dotaz.setString(1, kraj.getNazev());
        dotaz.setInt(2, kraj.getId());
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Nepodařilo se najít záznam.");
        }
        dotaz.close();
    }
    
    public static void upravitOkrsek(Okrsek okrsek) throws Exception {
        String textDotatu;
        provedenaZmena = true;
        PreparedStatement dotaz = pripojeni.prepareCall( textDotatu = 
                "UPDATE okrsky SET cislo = ? WHERE id = ?"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        dotaz.setInt(1, okrsek.getCislo());
        dotaz.setInt(2, okrsek.getId());
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Nepodařilo se najít záznam.");
        }
        dotaz.close();
    }
    
    public static void upravitStranu(Strana strana) throws Exception {
        String textDotatu;
        provedenaZmena = true;
        PreparedStatement dotaz = pripojeni.prepareCall( textDotatu = 
                "UPDATE strany SET zkratka = ?, cely_nazev = ?, poradove_cislo = ?, barva = ? WHERE id = ?"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        dotaz.setString(1, strana.getZkratka());
        dotaz.setString(2, strana.getCelyNazev());
        dotaz.setInt(3, strana.getPoradoveCislo());
        dotaz.setString(4, strana.getBarva());
        dotaz.setInt(5, strana.getId());
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Nepodařilo se najít záznam.");
        }
        dotaz.close();
    }
    
    public static void upravitKandidata(Kandidat kandidat) throws Exception {
        String textDotatu;
        provedenaZmena = true;
        PreparedStatement dotaz = pripojeni.prepareCall( textDotatu = 
                "UPDATE kandidati SET jmeno = ?, prijmeni = ?, poradove_cislo = ?, kraj_id = ? WHERE id = ?"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        dotaz.setString(1, kandidat.getJmeno());
        dotaz.setString(2, kandidat.getPrijmeni());
        dotaz.setInt(3, kandidat.getPoradoveCislo());
        dotaz.setInt(4, kandidat.getKrajID());
        dotaz.setInt(5, kandidat.getId());
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Nepodařilo se najít záznam.");
        }
        dotaz.close();
    }
    
    public static void upravitHlas(Hlas hlas) throws Exception {
        String textDotatu;
        provedenaZmena = true;
        PreparedStatement dotaz = pripojeni.prepareCall( textDotatu = 
                "UPDATE hlasy SET strana_id = ? WHERE id = ?"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        dotaz.setInt(1, hlas.getStranaID());
        dotaz.setInt(2, hlas.getId());
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Nepodařilo se najít záznam.");
        }
        dotaz.close();
    }
    
    public static void upravitPreferencniHlas(PreferencniHlas hlas) throws Exception {
        String textDotatu;
        provedenaZmena = true;
        PreparedStatement dotaz = pripojeni.prepareCall( textDotatu = 
                "UPDATE preferencni_hlasy SET kandidat_id = ? WHERE id = ?"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        dotaz.setInt(1, hlas.getKandidatID());
        dotaz.setInt(2, hlas.getId());
        if(dotaz.executeUpdate() < 1){
            dotaz.close();
            throw new Exception("Nepodařilo se najít záznam.");
        }
        dotaz.close();
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // METODY PRO ZÍSKÁNÍ VÝSLEDKŮ                                            //
    ////////////////////////////////////////////////////////////////////////////
    
    public static LinkedList<Vysledek> nactiVysledky(float limitProcent) throws Exception {
        String textDotatu;
        LinkedList<Vysledek> vysledky = new LinkedList<>();
        PreparedStatement dotaz = pripojeni.prepareCall( textDotatu = 
                "SELECT zkratka, procenta, barva FROM ( " + 
                    "SELECT zkratka, (pocet * 100.0) / (SELECT COUNT(*) FROM hlasy) procenta, barva FROM ( " +
                        "SELECT strana_id, COUNT(*) pocet FROM hlasy GROUP BY strana_id " +
                    ") JOIN strany ON strana_id = strany.id" +
                ") WHERE procenta >= ? ORDER BY procenta DESC"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        dotaz.setFloat(1, limitProcent);
        ResultSet data = dotaz.executeQuery();
        try {
            while (data.next()) {
                Vysledek vysledek = new Vysledek();
                vysledek.setZkratka(data.getString("zkratka"));
                vysledek.setProcenta(data.getInt("procenta"));
                vysledek.setBarva(data.getString("barva"));
                vysledky.add(vysledek);
            }
            return vysledky;
        } finally {
            data.close();
            dotaz.close();
        }
    }
    
    public static LinkedList<Vysledek> nactiVysledky(float limitProcent, Kraj kraj) throws Exception {
        String textDotatu;
        if(kraj == null) return new LinkedList<>();
        LinkedList<Vysledek> vysledky = new LinkedList<>();
        PreparedStatement dotaz = pripojeni.prepareCall( textDotatu = 
                "SELECT zkratka, procenta, barva FROM ( " +
                    "SELECT zkratka, (pocet * 100.0) / (SELECT COUNT(*) FROM hlasy JOIN okrsky ON okrsek_id = okrsky.id WHERE kraj_id = ?) procenta, barva FROM ( " +
                        "SELECT strana_id, COUNT(*) pocet FROM hlasy JOIN okrsky ON okrsek_id = okrsky.id WHERE kraj_id = ? GROUP BY strana_id " +
                    ") JOIN strany ON strana_id = strany.id" +
                ") WHERE procenta >= ? ORDER BY procenta DESC"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        dotaz.setInt(1, kraj.getId());
        dotaz.setInt(2, kraj.getId());
        dotaz.setFloat(3, limitProcent);
        ResultSet data = dotaz.executeQuery();
        try {
            while (data.next()) {
                Vysledek vysledek = new Vysledek();
                vysledek.setZkratka(data.getString("zkratka"));
                vysledek.setProcenta(data.getInt("procenta"));
                vysledek.setBarva(data.getString("barva"));
                vysledky.add(vysledek);
            }
            return vysledky;
        } finally {
            data.close();
            dotaz.close();
        }
    }
    
    public static LinkedList<Vysledek> nactiVysledky(float limitProcent, Okrsek okrsek) throws Exception {
        String textDotatu;
        if(okrsek == null) return new LinkedList<>();
        LinkedList<Vysledek> vysledky = new LinkedList<>();
        PreparedStatement dotaz = pripojeni.prepareCall( textDotatu = 
                "SELECT zkratka, procenta, barva FROM ( " + 
                    "SELECT zkratka, (pocet * 100.0) / (SELECT COUNT(*) FROM hlasy WHERE okrsek_id = ?) procenta, barva FROM ( " +
                        "SELECT strana_id, COUNT(*) pocet FROM hlasy WHERE okrsek_id = ? GROUP BY strana_id " +
                    ") JOIN strany ON strana_id = strany.id" +
                ") WHERE procenta >= ? ORDER BY procenta DESC"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        dotaz.setInt(1, okrsek.getId());
        dotaz.setInt(2, okrsek.getId());
        dotaz.setFloat(3, limitProcent);
        ResultSet data = dotaz.executeQuery();
        try {
            while (data.next()) {
                Vysledek vysledek = new Vysledek();
                vysledek.setZkratka(data.getString("zkratka"));
                vysledek.setProcenta(data.getFloat("procenta"));
                vysledek.setBarva(data.getString("barva"));
                vysledky.add(vysledek);
            }
            return vysledky;
        } finally {
            data.close();
            dotaz.close();
        }
    }
    
    public static ObservableList<Preference> nactiPreference(Strana strana, Kraj kraj) throws Exception {
        String textDotatu;
        if(strana == null) return FXCollections.observableArrayList();
        if(kraj == null) return FXCollections.observableArrayList();
        ObservableList<Preference> preference = FXCollections.observableArrayList();
        PreparedStatement dotaz = pripojeni.prepareCall( textDotatu = 
                "SELECT jmeno, prijmeni, procenta FROM (" +
                    "SELECT kandidat_id, (pocet * 100.0) / (" +
                        "SELECT COUNT(*) FROM hlasy " + 
                            "JOIN strany ON strana_id = strany.id " +
                            "JOIN okrsky ON okrsek_id = okrsky.id " + 
                        "WHERE strana_id = ? AND kraj_id = ? " + 
                    ") procenta FROM ( " +
                        "SELECT COUNT(*) pocet, kandidat_id FROM ( " +
                            "SELECT kandidat_id FROM preferencni_hlasy " + 
                                "JOIN hlasy ON hlas_id = hlasy.id " + 
                                "JOIN strany ON strana_id = strany.id " +
                                "JOIN okrsky ON okrsek_id = okrsky.id " + 
                            "WHERE strana_id = ? AND kraj_id = ? " + 
                        ") GROUP BY kandidat_id " +
                    ")" +
                ") JOIN kandidati ON kandidat_id = kandidati.id ORDER BY procenta DESC"
        );
        if(vypisPrikazuDoKonzole) System.out.println(textDotatu);
        dotaz.setInt(1, strana.getId());
        dotaz.setInt(2, kraj.getId());
        dotaz.setInt(3, strana.getId());
        dotaz.setInt(4, kraj.getId());
        ResultSet data = dotaz.executeQuery();
        try {
            while (data.next()) {
                Preference p = new Preference();
                p.setJmeno(data.getString("jmeno"));
                p.setPrijmeni(data.getString("prijmeni"));
                p.setProcenta(data.getFloat("procenta"));
                preference.add(p);
            }
            return preference;
        } finally {
            data.close();
            dotaz.close();
        }
    }
}
