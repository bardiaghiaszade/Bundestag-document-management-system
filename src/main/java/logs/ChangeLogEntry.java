package logs;

import java.time.LocalDateTime;

/**
 * Ein einzelner Eintrag im Änderungs-Log.
 * Ein Eintrag beantwortet die Fragen: welches Dokument, wer, wann, was und von welchem Wert auf welchen.
 * Als record ist ein Eintrag unveränderbar, d.h. man kann ihn nach der Erstellung nicht mehr ändern.
 * @param documentId id des Dokuments, das geändert wurde
 * @param userId     id des Benutzers, der die Änderung gemacht hat
 * @param timestamp  Zeitpunkt der Änderung
 * @param type       Art der Änderung
 * @param field      Name des geänderten Feldes (z.B. "title")
 * @param oldValue   Wert vor der Änderung ({@code null} z.B. beim Erstellen)
 * @param newValue   Wert nach der Änderung ({@code null} z.B. beim Entfernen)
 */
public record ChangeLogEntry(
        int documentId,
        int userId,
        LocalDateTime timestamp,
        ChangeType type,
        String field,
        String oldValue,
        String newValue
) {

    /**
     * Darstellung eines Eintrags, z.B. für die Ausgabe der Historie.
     * @return ein Text mit allen Angaben des Eintrags
     */
    @Override
    public String toString() {
        return timestamp + " | Dokument " + documentId + " | Benutzer " + userId
                + " | " + type + " | " + field + ": " + oldValue + " -> " + newValue;
    }
}