package main.docs;

import java.time.LocalDateTime;

/**
 * Ein Typ von Dokumenten.
 * Erbt die gemeinsamen Eigenschaften von Document Class
 */
public class Drucksache extends Document {

    /**
     * constructor
     * @param id id
     * @param title titel
     * @param isPublic ob man das z.B. als API Client sehen kann
     * @param text Inhalt
     * @param createdAt Erstellungsdatum
     * @param updatedAt Datum der letzten Änderung
     */
    public Drucksache(int id, String title, boolean isPublic,
                      String text, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, title, isPublic, text, createdAt, updatedAt); //ruft erstmal den constructor von superclass Document auf
    }

    //Alle getter/setter Funktionen werden von superclass Document geerbt
}