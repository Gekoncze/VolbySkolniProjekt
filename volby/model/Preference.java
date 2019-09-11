package volby.model;


public class Preference {
    private String jmeno;
    private String prijmeni;
    private float procenta;

    public Preference() {
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

    public float getProcenta() {
        return procenta;
    }

    public void setProcenta(float procenta) {
        this.procenta = procenta;
    }
}
