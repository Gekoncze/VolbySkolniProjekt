package volby.model;

import volby.Role;


public class Uzivatel {
    private int id;
    private String pristupoveJmeno;
    private String pristupoveHeslo;
    private String jmeno;
    private String prijmeni;
    private String telefon;
    private int role;

    public Uzivatel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPristupoveJmeno() {
        return pristupoveJmeno;
    }

    public void setPristupoveJmeno(String pristupoveJmeno) {
        this.pristupoveJmeno = pristupoveJmeno;
    }

    public String getPristupoveHeslo() {
        return pristupoveHeslo;
    }

    public void setPristupoveHeslo(String pristupoveHeslo) {
        this.pristupoveHeslo = pristupoveHeslo;
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

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }
    
    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
    
    public String getRoleString(){
        switch(role){
            case Role.SPRAVCE_UZIVATELU: return Role.SPRAVCE_UZIVATELU_TEXT;
            case Role.SPRAVCE_UDAJU: return Role.SPRAVCE_UDAJU_TEXT;
            case Role.ZADAVATEL_HLASU: return Role.ZADAVATEL_HLASU_TEXT;
            case Role.STATISTIK: return Role.STATISTIK_TEXT;
            default: return "Neznámý";
        }
    }
    
    public void setRoleString(String role){
        switch(role){
            case Role.SPRAVCE_UZIVATELU_TEXT: this.role = Role.SPRAVCE_UZIVATELU; break;
            case Role.SPRAVCE_UDAJU_TEXT: this.role = Role.SPRAVCE_UDAJU; break;
            case Role.ZADAVATEL_HLASU_TEXT: this.role = Role.ZADAVATEL_HLASU; break;
            case Role.STATISTIK_TEXT: this.role = Role.STATISTIK; break;
            default: this.role = -1; break;
        }
    }
}
