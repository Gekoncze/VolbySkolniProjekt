package volby.model;

import volby.Databaze;


public class Kandidat {
    private int id;
    private String jmeno;
    private String prijmeni;
    private int stranaID;
    private int krajID;
    private int poradoveCislo;
    private Kraj kraj;

    public Kandidat() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJmeno() {
        return jmeno;
    }

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    public String getPrijmeni() {
        return prijmeni;
    }

    public void setPrijmeni(String prijmeni) {
        this.prijmeni = prijmeni;
    }

    public int getStranaID() {
        return stranaID;
    }

    public void setStranaID(int stranaID) {
        this.stranaID = stranaID;
    }

    public int getKrajID() {
        return krajID;
    }

    public void setKrajID(int krajID) {
        this.krajID = krajID;
    }

    public int getPoradoveCislo() {
        return poradoveCislo;
    }

    public void setPoradoveCislo(int poradoveCislo) {
        this.poradoveCislo = poradoveCislo;
    }
    
    public String getPoradoveCisloText() {
        return poradoveCislo + "";
    }

    public void setPoradoveCisloText(String poradoveCislo) {
        this.poradoveCislo = Integer.parseInt(poradoveCislo);
    }

    public Kraj getKraj() {
        try {
            return Databaze.nactiKraj(krajID);
        } catch (Exception e) {
            return null;
        }
    }

    public void setKraj(Kraj kraj) {
        this.krajID = kraj.getId();
    }

    @Override
    public String toString() {
        return jmeno + " " + prijmeni;
    }
}
