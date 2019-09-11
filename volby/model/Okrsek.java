package volby.model;


public class Okrsek {
    private int id;
    private int cislo;
    private int krajID;

    public Okrsek() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCislo() {
        return cislo;
    }

    public void setCislo(int cislo) {
        this.cislo = cislo;
    }
    
    public String getCisloText() {
        return cislo + "";
    }

    public void setCisloText(String text) {
        this.cislo = Integer.parseInt(text);
    }

    public int getKrajID() {
        return krajID;
    }

    public void setKrajID(int krajID) {
        this.krajID = krajID;
    }

    @Override
    public String toString() {
        return cislo + "";
    }
}
