package volby.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import volby.Databaze;


public class Hlas {
    private int id;
    private int stranaID;
    private int uzivatelID;
    private int okrsekID;
    private Timestamp casVytvoreni;

    public Hlas() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStranaID() {
        return stranaID;
    }

    public void setStranaID(int stranaID) {
        this.stranaID = stranaID;
    }

    public int getUzivatelID() {
        return uzivatelID;
    }

    public void setUzivatelID(int uzivatelID) {
        this.uzivatelID = uzivatelID;
    }

    public int getOkrsekID() {
        return okrsekID;
    }

    public void setOkrsekID(int okrsekID) {
        this.okrsekID = okrsekID;
    }

    public Timestamp getCasVytvoreni() {
        return casVytvoreni;
    }

    public void setCasVytvoreni(Timestamp casVytvoreni) {
        this.casVytvoreni = casVytvoreni;
    }
    
    public Strana getStrana(){
        try {
            return Databaze.nactiStranu(stranaID);
        } catch (Exception e) {
            return null;
        }
    }
    
    public void setStrana(Strana strana){
        stranaID = strana.getId();
    }
    
    public String getCasVytvoreniText(){
        return new SimpleDateFormat("dd. MM. yyyy HH:mm:ss").format(casVytvoreni);
    }
}
