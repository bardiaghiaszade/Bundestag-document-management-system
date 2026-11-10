package users;

/**
 * Dies Interface setzt die Regel bezüglich Berechtigungen
 * und wird von subclasses implementiert
 */
public interface Berechtigung {

    //Alle sind public, weil von draußen aufgerufen werden, um permission zu überprüfen

    boolean darfBenutzerVerwalten();
    boolean darfDokumenteBearbeiten();
    boolean darfLogsBearbeiten();
}
