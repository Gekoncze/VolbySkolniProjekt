package volby;


public class PrihlaseniException extends Exception {
    public PrihlaseniException(){
        super("Chybně zadané uživatelské jméno nebo heslo.");
    }
}