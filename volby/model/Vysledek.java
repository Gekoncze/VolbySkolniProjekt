package volby.model;


public class Vysledek {
    private String zkratka;
    private float procenta;
    private String barva;

    public Vysledek() {
    }

    public String getZkratka() {
        return zkratka;
    }

    public void setZkratka(String zkratka) {
        this.zkratka = zkratka;
    }

    public float getProcenta() {
        return procenta;
    }

    public void setProcenta(float procenta) {
        this.procenta = procenta;
    }

    public String getBarva() {
        return barva;
    }

    public void setBarva(String barva) {
        this.barva = barva;
    }
}
