package users.human;

import users.Berechtigung;

/**
 * Logs-bearbeiter
 */
public class LogsBearbeiter extends HumanUsers {
    /**
     * constructor ohne partei
     * @param id id
     * @param first_name vorname
     * @param last_name nachname
     */
    public LogsBearbeiter(int id, String first_name, String last_name) {
        super(id, first_name, last_name);
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
        return true;
    }
}
