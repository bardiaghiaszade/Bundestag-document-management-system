package main.storage;

import main.docs.DocumentType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * So sieht ein Dokument in der JSON-Datei aus (ein Format für alle Dokumenttypen).
 * Felder, die für den Typ nicht relevant sind, bleiben null und werden nicht geschrieben.
 * @param type Dokumenttyp
 * @param id id
 * @param title titel
 * @param publicAccess ob das Dokument öffentlich ist
 * @param createdAt Erstellungsdatum
 * @param updatedAt Datum der letzten Änderung
 * @param text Inhalt
 * @param redner nur bei Rede
 * @param partei nur bei Rede
 * @param sitzungsnummer nur bei Protokoll
 * @param redeIds nur bei Protokoll: ids der Reden (die Reden selbst stehen separat in der Datei)
 */
public record DocumentJson(
        DocumentType type,
        int id,
        String title,
        boolean publicAccess,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String text,
        String redner,
        String partei,
        Integer sitzungsnummer,
        List<Integer> redeIds
) {}