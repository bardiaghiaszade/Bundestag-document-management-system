package docs;

import java.time.LocalDateTime;

/**
 * Ein Typ von Dokumenten.
 * Erbt die gemeinsamen Eigenschaften von Document Class
 */
public class Rede extends Document {

    private final String redner; //Kann nicht geändert werden
    private final String partei; //Kann nicht geändert werden

    /**
     * constructor
     * @param id id
     * @param title titel
     * @param isPublic ob man das z.B. als API Client sehen kann
     * @param redner Ein benutzer aus Abgeordneter Klasse
     * @param partei partei
     * @param text inhalt
     * @param createdAt Erstellungsdatum
     * @param updatedAt Datum der letzten Änderung
     */
    public Rede(int id, String title, boolean isPublic, String redner, String partei,
                String text, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, title, isPublic, text, createdAt, updatedAt); //ruft erstmal den constructor von superclass Document auf
        this.redner = redner;
        this.partei = partei;
    }

    /**
     * redner-getter
     * @return Die Person die gesprochen hat
     */
    public String getRedner() {
        return redner;
    }

    /**
     * partei-getter
     * @return Die Partei von der Person die gesprochen hat
     */
    public String getPartei() {
        return partei;
    }

    //Redner und Partei sind final und haben keinen setter.
    //Alle anderen getter/setter Funktionen werden von superclass Document geerbt
}