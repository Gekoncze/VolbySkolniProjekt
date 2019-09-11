package volby.model;

import volby.Databaze;


public class PreferencniHlas {
    private int id;
    private int hlasID;
    private int kandidatID;

    public PreferencniHlas() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHlasID() {
        return hlasID;
    }

    public void setHlasID(int hlasID) {
        this.hlasID = hlasID;
    }

    public int getKandidatID() {
        return kandidatID;
    }

    public void setKandidatID(int kandidatID) {
        this.kandidatID = kandidatID;
    }
    
    public Kandidat getKandidat(){
        try {
            return Databaze.nactiKandidata(kandidatID);
        } catch (Exception e) {
            return null;
        }
    }
}
