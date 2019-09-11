package volby.model;


public class Strana {
    private int id;
    private int poradoveCislo;
    private String zkratka;
    private String celyNazev;
    private String barva;

    public Strana() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPoradoveCislo() {
        return poradoveCislo;
    }

    public void setPoradoveCislo(int cislo) {
        this.poradoveCislo = cislo;
    }

    public String getZkratka() {
        return zkratka;
    }

    public void setZkratka(String zkratka) {
        this.zkratka = zkratka;
    }

    public String getCelyNazev() {
        return celyNazev;
    }

    public void setCelyNazev(String celyNazev) {
        this.celyNazev = celyNazev;
    }

    public String getBarva() {
        return barva;
    }

    public void setBarva(String barva) {
        this.barva = barva;
    }

    
    
    public String getPoradoveCisloText() {
        return poradoveCislo + "";
    }

    public void setPoradoveCisloText(String poradoveCislo) {
        this.poradoveCislo = Integer.parseInt(poradoveCislo);
    }

    @Override
    public String toString() {
        return zkratka;
    }
}
