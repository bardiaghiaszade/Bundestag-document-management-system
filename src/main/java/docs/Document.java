package docs;

import java.time.LocalDateTime;

/**
 * Abstrakte Basisklasse für alle Dokumenttypen des Dokumentenmanagementsystems.
 * Dies enthält gemeinsame Eigenschaften wie ID, Titel, Erstellungsdatum
 */

public abstract class Document {

    // Alle protected damit man sie innerhalb eines packages erreichen kann
    protected final int id; //Kann nicht geändert werden
    protected String title;
    protected final LocalDateTime createdAt; //Kann nicht geändert werden
    protected LocalDateTime updatedAt;
    protected boolean isPublic;
    protected  String text;

    /**
     *constructor
     * @param id id von Dokument (final)
     * @param title title von Dokument
     * @param isPublic ob man das z.B. als API Client sehen kann
     */
    protected Document(int id, String title, Boolean isPublic, String text) {
        this.id = id;
        this.title = title;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt; //Am Anfang ist es letztes Mal beim Erstellen aktualisiert
        this.isPublic = isPublic;
        this.text = text;
    }

    //Alle getter Funktionen hier sind nur innerhalb dieses packages erreichbar weil die protected sind

    /**
     * id-getter
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * titel-getter
     * @return titel
     */
    public String getTitle() {
        return title;
    }

    /**
     * createAt-getter
     * @return Datum von Erstellung
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * text-getter
     * @return inhalt des Dokuments
     */
    public String getText() {
        return text;
    }

    /**
     * updateAt-getter
     * @return Datum von letzter Aktualisierung
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * isPublic-getter
     * @return ein boolean der zeigt, ob Dokument öffentlich ist
     */
    public boolean isPublic() {
        return isPublic;
    }

     //Man kann nur für UpdateDatum, Permission und Titel eine setter-Funktion definieren
     //CreateDatum oder ID sind final und haben keinen setter!

    /**
     * title-setter
     * @param title neuer titel
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * updateAt-setter
     * @param updatedAt Datum von letzter Aktualisierung
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * text-setter
     * @param text neuer inhalt
     */
    public void setText(String text) {
        this.text = text;
    }
    
    /**
     * isPublic-setter
     * @param isPublic ein boolean der zeigt, ob Dokument öffentlich ist
     */
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }


}
