package volby;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;
import javafx.collections.ObservableList;
import volby.model.Hlas;
import volby.model.Kandidat;
import volby.model.Kraj;
import volby.model.Okrsek;
import volby.model.PreferencniHlas;
import volby.model.Strana;
import volby.model.Uzivatel;
import volby.skripty.LokatorSkriptu;
import volby.texty.LokatorTextu;


public class Generator {
    private static final ArrayList<String> jmenaZeny = new ArrayList<>();
    private static final ArrayList<String> jmenaMuzi = new ArrayList<>();
    private static final ArrayList<String> prijmeniZeny = new ArrayList<>();
    private static final ArrayList<String> prijmeniMuzi = new ArrayList<>();
    private static final Random nahoda = new Random(1);
    private static final float genderovaVyvazenost = 0.5f;
    private static final int minimalniPocetOkrsku = 5;
    private static final int maximalniPocetOkrsku = 15;
    private static final int minimalniPocetKandidatu = 3;
    private static final int maximalniPocetKandidatu = 12;
    private static final int minimalniPocetHlasuVOkrsku = 20;
    private static final int maximalniPocetHlasuVOkrsku = 30;
    private static final int uyivatelID = 3;
    private static final float pravdepodobnostUdeleniHlasu = 0.2f;
    private static final int maximalniPocetPreferencnichHlasu = 4;
    private static final HashMap<String, Integer> pozice = new HashMap<String, Integer>();
    private static int celkovaVaha = 0;
    
    public static void main(String[] args) throws Exception {
        System.out.println("### Vytvářím novou databázi");
        vytvoritDatabazi();
        
        System.out.println("### Vytvářím nová data");
        nactiJmenaPrijmeni();
        vytvoritData();
    }
    
    private static void nastavitVahu(String zk, Integer vaha){
        pozice.put(zk, celkovaVaha + vaha);
        celkovaVaha += vaha;
    }
    
    private static float zjistitLimit(String zk){
        Integer p = pozice.get(zk);
        if(p == null) return 0;
        else return p / (float)celkovaVaha;
    }
    
    private static Strana vylosovatStranu(ObservableList<Strana> strany){
        Strana s = null;
        do {
            float cislo = nahoda.nextFloat();
            float nejmensiLimit = 1;
            s = null;
            for(Strana strana : strany){
                float aktualniLimit = zjistitLimit(strana.getZkratka());
                if(cislo < aktualniLimit && aktualniLimit < nejmensiLimit){
                    nejmensiLimit = aktualniLimit;
                    s = strana;
                }
            }
        } while (s == null);
        return s;
    }
    
    private static void nactiJmenaPrijmeni() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(LokatorTextu.class.getResourceAsStream("jmena_zeny.txt")));
        String jmeno;
        while((jmeno = br.readLine()) != null){
            jmenaZeny.add(jmeno);
        }
        br = new BufferedReader(new InputStreamReader(LokatorTextu.class.getResourceAsStream("jmena_muzi.txt")));
        while((jmeno = br.readLine()) != null){
            jmenaMuzi.add(jmeno);
        }
        br = new BufferedReader(new InputStreamReader(LokatorTextu.class.getResourceAsStream("prijmeni.txt")));
        while((jmeno = br.readLine()) != null){
            if(jmeno.endsWith("á")){
                prijmeniZeny.add(jmeno);
            } else {
                prijmeniMuzi.add(jmeno);
            }
        }
    }
    
    private static String[] vytvorJmenoPrijmeni(){
        boolean zena = nahoda.nextFloat() < genderovaVyvazenost;
        if(zena){
            int cisloJmena = (int) (nahoda.nextFloat() * jmenaMuzi.size());
            int cisloPrijmeni = (int) (nahoda.nextFloat() * prijmeniMuzi.size());
            return new String[]{ jmenaMuzi.get(cisloJmena), prijmeniMuzi.get(cisloPrijmeni) };
        } else {
            int cisloJmena = (int) (nahoda.nextFloat() * jmenaZeny.size());
            int cisloPrijmeni = (int) (nahoda.nextFloat() * prijmeniZeny.size());
            return new String[]{ jmenaZeny.get(cisloJmena), prijmeniZeny.get(cisloPrijmeni) };
        }
    }
    
    private static void vytvoritData() throws Exception {
        Databaze.setVypisPrikazuDoKonzole(true);
        Databaze.prihlasit("admin", Hashovani.vytvoritHash("admin"));
        vytvoritUzivatele();
        vytvoritKraje();
        vytvoritOkrsky();
        vytvoritStrany();
        vytvoritKandidaty();
        Databaze.ulozitZmeny();
        Databaze.odhlasit();
        Databaze.prihlasit("lojza", Hashovani.vytvoritHash("lojza"));
        vytvoritHlasy();
        vytvoritPreferencniHlasy();
        Databaze.ulozitZmeny();
        Databaze.odhlasit();
    }
    
    private static void vytvoritUzivatele() throws Exception {
        Uzivatel uzivatel = new Uzivatel();
        
        uzivatel.setRole(Role.SPRAVCE_UDAJU);
        uzivatel.setPristupoveJmeno("franta");
        uzivatel.setPristupoveHeslo(Hashovani.vytvoritHash("franta"));
        uzivatel.setJmeno("František");
        uzivatel.setPrijmeni("Papež");
        uzivatel.setTelefon("234 567 890");
        Databaze.novyUzivatel(uzivatel);
        
        uzivatel.setRole(Role.ZADAVATEL_HLASU);
        uzivatel.setPristupoveJmeno("lojza");
        uzivatel.setPristupoveHeslo(Hashovani.vytvoritHash("lojza"));
        uzivatel.setJmeno("Josef");
        uzivatel.setPrijmeni("Nadaný");
        uzivatel.setTelefon("345 678 901");
        Databaze.novyUzivatel(uzivatel);
        
        uzivatel.setRole(Role.STATISTIK);
        uzivatel.setPristupoveJmeno("marek");
        uzivatel.setPristupoveHeslo(Hashovani.vytvoritHash("marek"));
        uzivatel.setJmeno("Josef");
        uzivatel.setPrijmeni("Marek");
        uzivatel.setTelefon("456 789 012");
        Databaze.novyUzivatel(uzivatel);
    }
    
    private static void vytvoritKraje() throws Exception {
        Kraj kraj = new Kraj();
        
        kraj.setNazev("Pardubický kraj");
        Databaze.novyKraj(kraj);
        
        kraj.setNazev("Královéhradecký kraj");
        Databaze.novyKraj(kraj);
        
        kraj.setNazev("Liberecký kraj");
        Databaze.novyKraj(kraj);
        
        kraj.setNazev("Ústecký kraj");
        Databaze.novyKraj(kraj);
        
        kraj.setNazev("Karlovarský kraj");
        Databaze.novyKraj(kraj);
        
        kraj.setNazev("Plzeňský kraj");
        Databaze.novyKraj(kraj);
        
        kraj.setNazev("Jihočeský kraj");
        Databaze.novyKraj(kraj);
        
        kraj.setNazev("Kraj Vysočina");
        Databaze.novyKraj(kraj);
        
        kraj.setNazev("Jihomoravský kraj");
        Databaze.novyKraj(kraj);
        
        kraj.setNazev("Zlínský kraj");
        Databaze.novyKraj(kraj);
        
        kraj.setNazev("Moravskoslezský kraj");
        Databaze.novyKraj(kraj);
        
        kraj.setNazev("Olomoucký kraj");
        Databaze.novyKraj(kraj);
        
        kraj.setNazev("Středočeský kraj");
        Databaze.novyKraj(kraj);
        
        kraj.setNazev("Praha");
        Databaze.novyKraj(kraj);
    }
    
    private static void vytvoritOkrsky() throws Exception {
        ObservableList<Kraj> kraje = Databaze.nactiKraje();
        int k = 1000;
        for(Kraj kraj : kraje){
            int pocetOkrsku = (int) (minimalniPocetOkrsku + nahoda.nextFloat() * (maximalniPocetOkrsku - minimalniPocetOkrsku));
            for(int i = 0; i < pocetOkrsku; i++){
                Okrsek okrsek = new Okrsek();
                okrsek.setKrajID(kraj.getId());
                okrsek.setCislo(k + i);
                Databaze.novyOkrsek(okrsek);
            }
            k += 1000;
        }
    }
    
    private static void vytvoritStrany() throws Exception {
        Strana strana = new Strana();
        int poradoveCislo = 1;
        
        nastavitVahu("ČSSN", 3);
        strana.setZkratka("ČSSN");
        strana.setCelyNazev("Česká strana sociálně nekromantická");
        strana.setPoradoveCislo(poradoveCislo);
        strana.setBarva("#ffa100");
        Databaze.novaStrana(strana); poradoveCislo++;
        
        nastavitVahu("ONS", 4);
        strana.setZkratka("ONS");
        strana.setCelyNazev("Občanská nekromantická strana");
        strana.setPoradoveCislo(poradoveCislo);
        strana.setBarva("#1f59ff");
        Databaze.novaStrana(strana); poradoveCislo++;
        
        nastavitVahu("Bottom 90", 1);
        strana.setZkratka("Bottom 90");
        strana.setCelyNazev("Bottom 90");
        strana.setPoradoveCislo(poradoveCislo);
        strana.setBarva("#6638ff");
        Databaze.novaStrana(strana); poradoveCislo++;
        
        nastavitVahu("Přírodní", 1);
        strana.setZkratka("Přírodní");
        strana.setCelyNazev("Přírodní");
        strana.setPoradoveCislo(poradoveCislo);
        strana.setBarva("#1dcc00");
        Databaze.novaStrana(strana); poradoveCislo++;
        
        nastavitVahu("BNU-ČSZ", 2);
        strana.setZkratka("BNU-ČSZ");
        strana.setCelyNazev("Budhistická a nekromantická unie – Československá strana zvířecí");
        strana.setPoradoveCislo(poradoveCislo);
        strana.setBarva("#f5ed00");
        Databaze.novaStrana(strana); poradoveCislo++;
        
        nastavitVahu("BAZ", 2);
        strana.setZkratka("BAZ");
        strana.setCelyNazev("Bezdomovci a závislí");
        strana.setPoradoveCislo(poradoveCislo);
        strana.setBarva("#989898");
        Databaze.novaStrana(strana); poradoveCislo++;
        
        nastavitVahu("Nomádi", 4);
        strana.setZkratka("Nomádi");
        strana.setCelyNazev("Nomádi");
        strana.setPoradoveCislo(poradoveCislo);
        strana.setBarva("#000000");
        Databaze.novaStrana(strana); poradoveCislo++;
        
        nastavitVahu("NE", 5);
        strana.setZkratka("NE");
        strana.setCelyNazev("NE");
        strana.setPoradoveCislo(poradoveCislo);
        strana.setBarva("#00aeff");
        Databaze.novaStrana(strana); poradoveCislo++;
        
        nastavitVahu("SPN", 3);
        strana.setZkratka("SPN");
        strana.setCelyNazev("Strana přímé nekromancie");
        strana.setPoradoveCislo(poradoveCislo);
        strana.setBarva("#d6692b");
        Databaze.novaStrana(strana); poradoveCislo++;
        
        nastavitVahu("Surrealisté", 1);
        strana.setZkratka("Surrealisté");
        strana.setCelyNazev("Surrealisté");
        strana.setPoradoveCislo(poradoveCislo);
        strana.setBarva("#c96acf");
        Databaze.novaStrana(strana); poradoveCislo++;
        
        nastavitVahu("KSČR", 3);
        strana.setZkratka("KSČR");
        strana.setCelyNazev("Komančové Číny a Ruska");
        strana.setPoradoveCislo(poradoveCislo);
        strana.setBarva("#ff1f00");
        Databaze.novaStrana(strana); poradoveCislo++;
    }
    
    private static void vytvoritKandidaty() throws Exception {
        ObservableList<Strana> strany = Databaze.nactiStrany();
        ObservableList<Kraj> kraje = Databaze.nactiKraje();
        for(Strana strana : strany){
            int pc = 1;
            for(Kraj kraj : kraje){
                int pocetKandidatu = (int) (minimalniPocetKandidatu + nahoda.nextFloat() * (maximalniPocetKandidatu - minimalniPocetKandidatu));
                for(int i = 0; i < pocetKandidatu; i++){
                    String[] jmenoPrijmeni = vytvorJmenoPrijmeni();
                    Kandidat kandidat = new Kandidat();
                    kandidat.setStranaID(strana.getId());
                    kandidat.setJmeno(jmenoPrijmeni[0]);
                    kandidat.setPrijmeni(jmenoPrijmeni[1]);
                    kandidat.setKrajID(kraj.getId());
                    kandidat.setPoradoveCislo(pc);
                    Databaze.novyKandidat(kandidat);
                    pc++;
                }
            }
        }
    }
    
    private static void vytvoritHlasy() throws Exception {
        ObservableList<Strana> strany = Databaze.nactiStrany();
        ObservableList<Kraj> kraje = Databaze.nactiKraje();
        for(Kraj kraj : kraje){
            ObservableList<Okrsek> okrsky = Databaze.nactiOkrsky(kraj);
            for(Okrsek okrsek : okrsky){
                int pocetHlasu = (int) (minimalniPocetHlasuVOkrsku + nahoda.nextFloat() * (maximalniPocetHlasuVOkrsku - minimalniPocetHlasuVOkrsku));
                for(int i = 0; i < pocetHlasu; i++){
                    Hlas hlas = new Hlas();
                    hlas.setOkrsekID(okrsek.getId());
                    hlas.setStranaID(vylosovatStranu(strany).getId());
                    hlas.setUzivatelID(Databaze.getUzivatel().getId());
                    Databaze.novyHlas(hlas);
                }
            }
        }
    }
    
    private static void vytvoritPreferencniHlasy() throws Exception {
        ObservableList<Kraj> kraje = Databaze.nactiKraje();
        for(Kraj kraj : kraje){
            ObservableList<Okrsek> okrsky = Databaze.nactiOkrsky(kraj);
            for(Okrsek okrsek : okrsky){
                ObservableList<Hlas> hlasy = Databaze.nactiHlasy(okrsek);
                for(Hlas hlas : hlasy){
                    boolean udelitPreferencniHlasy = nahoda.nextFloat() < pravdepodobnostUdeleniHlasu;
                    if(udelitPreferencniHlasy){
                        int pocetHlasu = (int) (nahoda.nextFloat() * maximalniPocetPreferencnichHlasu + 1);
                        if(pocetHlasu > maximalniPocetPreferencnichHlasu) continue;
                        Strana strana = Databaze.nactiStranu(hlas.getStranaID());
                        ObservableList<Kandidat> kandidati = Databaze.nactiKandidaty(strana);
                        int[] cisla = new int[maximalniPocetPreferencnichHlasu];
                        for(int j = 0; j < pocetHlasu; j++){
                            float nahodneCislo = nahoda.nextFloat();
                            nahodneCislo = nahodneCislo * nahodneCislo * nahodneCislo;
                            int cislo = (int) (nahodneCislo * kandidati.size());
                            for(int k = 0; k < j - 1; k++) if(cislo == cisla[k]) break;
                            cisla[j] = cislo;
                            PreferencniHlas phlas = new PreferencniHlas();
                            phlas.setHlasID(hlas.getId());
                            phlas.setKandidatID(kandidati.get(cislo).getId());
                            Databaze.novyPreferencniHlas(phlas);
                        }
                    }
                }
            }
        }
    }
    
    
    private static void vytvoritDatabazi() throws Exception {
        Connection pripojeni;
        Class.forName("oracle.jdbc.OracleDriver");
        pripojeni = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "admin", "admin");
        pripojeni.setAutoCommit(false);
        
        spustitSkript(pripojeni, nacistSkript("SQL_CLEANUP.txt"));
        spustitSkript(pripojeni, nacistSkript("SQL_CREATE.txt"));
        spustitSkript(pripojeni, nacistSkript("SQL_INSERT_ADMIN.txt"));
        
        pripojeni.commit();
        pripojeni.close();
    }
    
    private static String nacistSkript(String nazevSouboru) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(LokatorSkriptu.class.getResourceAsStream(nazevSouboru)));
        String radek = "";
        String skript = "";
        while((radek = br.readLine()) != null) skript += radek + "\n";
        return skript;
    }
    
    private static void spustitSkript(Connection pripojeni, String skript) throws Exception {
        StringTokenizer tokenizerPrikazu = new StringTokenizer(skript, ";");
        while(tokenizerPrikazu.hasMoreTokens()){
            String prikaz = tokenizerPrikazu.nextToken();
            if(prikaz.trim().length() <= 0) continue;
            try {
                spustitPrikaz(pripojeni, prikaz.trim());
            } catch(SQLException e){
                System.out.println("CHYBA: " + e.getMessage());
            }
        }
    }
    
    private static void spustitPrikaz(Connection pripojeni, String prikaz) throws Exception {
        System.out.println(prikaz);
        PreparedStatement dotaz = pripojeni.prepareCall(prikaz);
        dotaz.executeUpdate();
    }
}
