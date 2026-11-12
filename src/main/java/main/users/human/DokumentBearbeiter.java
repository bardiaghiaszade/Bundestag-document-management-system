package main.users.human;

/**
 * Dokument-bearbeiter
 */
public class DokumentBearbeiter extends HumanUsers {
    /**
     * constructor ohne partei
     * @param id id
     * @param first_name vorname
     * @param last_name nachname
     */
    public DokumentBearbeiter(int id, String first_name, String last_name) {
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
        return true;
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
