package storage;

import docs.Document;
import logs.ChangeLogEntry;

import java.util.List;

/**
 * Schnittstelle zum Speichern und Laden der Dokumente und des Änderungs-Logs.
 * Die Dateien sind die einzige Quelle der Daten, es gibt keine Listen im Speicher:
 * jede Änderung wird sofort gespeichert.
 * Bei Problemen mit der Datei wird eine {@link java.io.UncheckedIOException} geworfen.
 */
public interface DocumentStorage {

    /**
     * Speichert ein Dokument (ein Dokument mit gleicher id wird ersetzt)
     * @param document das Dokument
     */
    void saveDocument(Document document);

    /**
     * Lädt alle Dokumente (leere Liste, wenn nichts gespeichert ist)
     * @return alle Dokumente
     */
    List<Document> loadAllDocuments();

    /**
     * Hängt einen Eintrag hinten an das Änderungs-Log an
     * @param entry der neue Eintrag
     */
    void appendLogEntry(ChangeLogEntry entry);

    /**
     * Lädt das gesamte Änderungs-Log (älteste Einträge zuerst)
     * @return alle Einträge
     */
    List<ChangeLogEntry> loadLogEntries();
}