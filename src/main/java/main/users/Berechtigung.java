package main.users;

/**
 * Dies Interface setzt die Regel bezüglich Berechtigungen
 * und wird von subclasses implementiert
 */
public interface Berechtigung {

    //Alle sind public, weil von draußen aufgerufen werden, um permission zu überprüfen

    boolean darfBenutzerVerwalten(); //später muss entwickelt werden
    boolean darfDokumenteBearbeiten();
    boolean darfLogsBearbeiten();
}
