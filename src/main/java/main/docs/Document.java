package main.docs;

import java.time.LocalDateTime;

/**
 * Abstrakte Basisklasse für alle Dokumenttypen des Dokumentenmanagementsystems.
 * Dies enthält gemeinsame Eigenschaften wie ID, Titel, Erstellungsdatum
 */

public abstract class Document {

    // Alle private: von außen nur über getter lesbar und nur durch den Editor änderbar (Datenkapselung)
    private final int id; //Kann nicht geändert werden
    private String title;
    private final LocalDateTime createdAt; //Kann nicht geändert werden
    private LocalDateTime updatedAt;
    private boolean isPublic;
    private String text;

    /**
     * constructor
     * @param id id von Dokument (final)
     * @param title title von Dokument
     * @param isPublic ob man das z.B. als API Client sehen kann
     * @param text text
     * @param createdAt Erstellungsdatum (neues Dokument: jetzt, aus JSON geladen: das gespeicherte Datum)
     * @param updatedAt Datum der letzten Änderung (neues Dokument: genau wie bei createdAt)
     */
    protected Document(int id, String title, boolean isPublic, String text,
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isPublic = isPublic;
        this.text = text;
    }

    /**
     * Zwei Dokumente sind gleich, wenn sie vom gleichen Typ sind und die gleiche id haben.
     * Wichtig, weil bei jedem Laden aus der Datei neue Objekte entstehen
     * (sonst würde z.B. redeListe.contains(rede) nicht funktionieren).
     * @param o das andere Objekt
     * @return true, wenn beide dasselbe Dokument sind
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof Document d && d.getClass() == getClass() && d.id == id;
    }

    /**
     * hashCode passend zu equals
     * @return die id
     */
    @Override
    public int hashCode() {
        return id;
    }

    //Alle getter sind public, die setter sind package-private (nur der Editor darf ändern)

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

    //Man kann nur für UpdateDatum, Permission, Text und Titel eine setter-Funktion definieren
    //CreateDatum oder ID sind final und haben keinen setter!

    /**
     * title-setter
     * es gibt kein <public> weil package-private ist und nur in package docs und
     * durch editor kann geändert werden
     * @param title neuer titel
     */
    void setTitle(String title) {
        this.title = title;
    }

    /**
     * updateAt-setter
     * es gibt kein <public> weil package-private ist und nur in package docs und
     * durch editor kann geändert werden
     * @param updatedAt Datum von letzter Aktualisierung
     */
    void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * text-setter
     * es gibt kein <public> weil package-private ist und nur in package docs und
     * durch editor kann geändert werden
     * @param text neuer inhalt
     */
    void setText(String text) {
        this.text = text;
    }

    /**
     * isPublic-setter
     * es gibt kein <public> weil package-private ist und nur in package docs und
     * durch editor kann geändert werden
     * @param isPublic ein boolean der zeigt, ob Dokument öffentlich ist
     */
    void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
}