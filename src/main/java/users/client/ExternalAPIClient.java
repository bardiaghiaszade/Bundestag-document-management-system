package users.client;

import users.Berechtigung;

/**
 * Diese Klasse ist bezüglich external API CLIents und ob die die Dokumente
 * zugreifen dürfen, wird durch isPublic(Attribute von einem Dokument) entschieden
 */
public class ExternalAPIClient implements Berechtigung {

    protected final int id;
    protected final String apiKeyHash;
    protected boolean isActive;


    /**
     * constructor
     * @param id is
     * @param isActive ob client active ist
     * @param apiKeyHash security =D
     */
    public ExternalAPIClient(int id, boolean isActive, String apiKeyHash){
        this.id = id;
        this.apiKeyHash = apiKeyHash;
        this.isActive = true;
    }

    //Alle getter Funktionen hier sind nur innerhalb dieses packages erreichbar, weil die protected sind

    /**
     * id-getter
     * @return id
     */
    public int getId(){
        return id;
    }

    /**
     * apiKeyHash-getter
     * @return apiKeyHash
     */
    public String getApiKeyHash(){
        return apiKeyHash;
    }

    /**
     * isActive-getter
     * @return isActive Situation
     */
    public boolean getIsActive(){
        return isActive;
    }


    // setter kann man nur für isActive schreiben um das aktualisieren zu können

    /**
     * isActive-setter (aktualisieren)
     * @param isActive isActive
     */
    public void setPartei(boolean isActive) {
        this.isActive = isActive;
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
