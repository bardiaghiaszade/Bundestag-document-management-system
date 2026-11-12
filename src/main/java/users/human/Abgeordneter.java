package users.human;

import users.Berechtigung;

/**
 * Abgeordneter
 */
public class Abgeordneter extends HumanUsers  {
    /**
     * constructor mit partei
     * @param id id
     * @param first_name vorname
     * @param last_name nachname
     */
    public Abgeordneter(int id, String first_name, String last_name, String partei) {
        super(id, first_name, last_name, partei);
    }

    /**
     * überschreiben von einem Attribute aus Berechtigung Interface
     * @return permission
     */
    @Override
    public boolean darfBenutzerVerwalten() {
        return false;
    }

    /**
     * überschreiben von einem Attribute aus Berechtigung Interface
     * @return permission
     */
    @Override
    public boolean darfDokumenteBearbeiten() {
        return false;
    }

    /**
     * überschreiben von einem Attribute aus Berechtigung Interface
     * @return permission
     */
    @Override
    public boolean darfLogsBearbeiten() {
        return false;
    }
}
