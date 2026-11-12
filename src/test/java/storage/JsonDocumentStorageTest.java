package storage;

import docs.Document;
import docs.Drucksache;
import docs.Protokoll;
import docs.Rede;
import logs.ChangeLogEntry;
import logs.ChangeType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class JsonDocumentStorageTest {

    @TempDir
    Path dir; //temporärer Ordner, wird nach jedem Test gelöscht

    @Test
    void savedDocumentsAndLogAreLoadedAgain() {
        LocalDateTime created = LocalDateTime.of(2026, 3, 1, 10, 0);
        LocalDateTime updated = LocalDateTime.of(2026, 3, 2, 11, 30);
        Drucksache drucksache = new Drucksache(1, "Antrag", true, "Text A", created, updated);
        Rede rede = new Rede(2, "Rede", false, "Anna Beispiel", "Partei A", "Text B", created, updated);
        Protokoll protokoll = new Protokoll(3, "Protokoll", true, 57, "Text C", created, updated, List.of(rede));
        ChangeLogEntry entry = new ChangeLogEntry(1, 1, created, ChangeType.CREATED, "document", null, "Antrag");

        JsonDocumentStorage storage = new JsonDocumentStorage(
                dir.resolve("documents.json"), dir.resolve("changelog.json"));
        storage.saveDocument(drucksache);
        storage.saveDocument(rede);
        storage.saveDocument(protokoll);
        storage.appendLogEntry(entry);

        List<Document> loaded = storage.loadAllDocuments();
        assertEquals(3, loaded.size());
        assertEquals("Antrag", loaded.get(0).getTitle());
        assertEquals(updated, loaded.get(0).getUpdatedAt()); //Datum bleibt beim Laden erhalten
        assertEquals("Anna Beispiel", ((Rede) loaded.get(1)).getRedner());
        Protokoll p = (Protokoll) loaded.get(2);
        assertEquals(57, p.getSitzungsnummer());
        assertSame(loaded.get(1), p.getRedeListe().getFirst()); //dieselbe Rede, keine Kopie
        assertEquals(List.of(entry), storage.loadLogEntries());
    }

    @Test
    void savingTheSameIdReplacesTheDocument() {
        LocalDateTime time = LocalDateTime.of(2026, 3, 1, 10, 0);
        JsonDocumentStorage storage = new JsonDocumentStorage(
                dir.resolve("documents.json"), dir.resolve("changelog.json"));

        storage.saveDocument(new Drucksache(1, "Alt", true, "Text", time, time));
        storage.saveDocument(new Drucksache(1, "Neu", true, "Text", time, time));

        assertEquals(1, storage.loadAllDocuments().size());
        assertEquals("Neu", storage.loadAllDocuments().getFirst().getTitle());
    }
}