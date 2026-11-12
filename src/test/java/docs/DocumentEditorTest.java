package docs;

import factory.DocumentData;
import logs.ChangeLogEntry;
import logs.ChangeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import storage.JsonDocumentStorage;
import users.client.ExternalAPIClient;
import users.human.Abgeordneter;
import users.human.DokumentBearbeiter;
import users.human.LogsBearbeiter;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentEditorTest {

    @TempDir
    Path dir; //temporärer Ordner, wird nach jedem Test gelöscht

    private JsonDocumentStorage storage;
    private DocumentEditor editor;
    private final DokumentBearbeiter anna = new DokumentBearbeiter(1, "Anna", "Schmidt");
    private final LogsBearbeiter lars = new LogsBearbeiter(2, "Lars", "Meier");
    private final Abgeordneter max = new Abgeordneter(3, "Max", "Mustermann", "Partei B");

    @BeforeEach
    void setUp() {
        storage = new JsonDocumentStorage(dir.resolve("documents.json"), dir.resolve("changelog.json"));
        editor = new DocumentEditor(storage);
    }

    /**
     * Hilfsmethode: erstellt eine Drucksache
     */
    private Document createDrucksache(int id, boolean isPublic) {
        return editor.createDocument(anna, new DocumentData(
                DocumentType.DRUCKSACHE, id, "Antrag " + id, isPublic, null, null, "Text", null));
    }

    /**
     * Hilfsmethode: lädt ein Dokument frisch aus der Datei
     */
    private Document load(int id) {
        return storage.loadAllDocuments().stream()
                .filter(d -> d.getId() == id).findFirst().orElseThrow();
    }

    @Test
    void changeIsSavedAndLogged() {
        Document doc = createDrucksache(1, true);

        editor.changeTitle(doc, anna, "Neuer Titel");

        assertEquals("Neuer Titel", load(1).getTitle());
        List<ChangeLogEntry> history = editor.getHistory(1, lars);
        assertEquals(2, history.size()); //CREATED und TITLE_CHANGED
        assertEquals(ChangeType.TITLE_CHANGED, history.get(1).type());
        assertEquals("Antrag 1", history.get(1).oldValue());
        assertEquals("Neuer Titel", history.get(1).newValue());
    }

    @Test
    void unchangedValueCreatesNoLogEntry() {
        Document doc = createDrucksache(1, true);

        editor.changeTitle(doc, anna, doc.getTitle());

        assertEquals(1, editor.getHistory(1, lars).size()); //nur CREATED
    }

    @Test
    void withoutPermissionNothingIsChanged() {
        Document doc = createDrucksache(1, true);

        assertThrows(SecurityException.class, () -> editor.changeText(doc, max, "Unerlaubt"));
        assertThrows(SecurityException.class, () -> editor.createDocument(max, new DocumentData(
                DocumentType.DRUCKSACHE, 2, "Antrag 2", true, null, null, "Text", null)));

        assertEquals("Text", load(1).getText());
        assertEquals(1, storage.loadAllDocuments().size());
        assertEquals(1, storage.loadLogEntries().size()); //nur CREATED von Dokument 1
    }

    @Test
    void duplicateIdIsRejected() {
        createDrucksache(1, true);

        assertThrows(IllegalArgumentException.class, () -> createDrucksache(1, true));

        assertEquals(1, storage.loadAllDocuments().size());
    }

    @Test
    void redenStayInProtokollAfterReload() {
        Rede rede = (Rede) editor.createDocument(anna, new DocumentData(
                DocumentType.REDE, 2, "Rede", true, "Anna Beispiel", "Partei A", "Text", null));
        Protokoll protokoll = (Protokoll) editor.createDocument(anna, new DocumentData(
                DocumentType.PROTOKOLL, 3, "Protokoll", true, null, null, "Text", 57));
        editor.addRede(protokoll, rede, anna);

        //neu aus der Datei laden: die Rede muss noch im Protokoll sein
        Protokoll reloaded = (Protokoll) load(3);
        assertEquals(1, reloaded.getRedeListe().size());
        assertEquals(2, reloaded.getRedeListe().getFirst().getId());

        //eine Änderung am neu geladenen Protokoll darf die Reden nicht löschen
        editor.changeTitle(reloaded, anna, "Neuer Titel");
        assertEquals(1, ((Protokoll) load(3)).getRedeListe().size());

        //dieselbe Rede nochmal hinzufügen geht nicht (auch mit neu geladenen Objekten)
        assertThrows(IllegalStateException.class,
                () -> editor.addRede((Protokoll) load(3), (Rede) load(2), anna));
    }

    @Test
    void onlyLogsBearbeiterSeesHistory() {
        createDrucksache(1, true);

        assertThrows(SecurityException.class, () -> editor.getHistory(1, anna));
    }

    @Test
    void apiClientSeesOnlyPublicDocuments() {
        createDrucksache(1, true);
        createDrucksache(2, false);

        List<Document> visible = editor.getPublicDocuments(new ExternalAPIClient(1, true, "hash"));

        assertEquals(1, visible.size());
        assertEquals(1, visible.getFirst().getId());
    }

    @Test
    void inactiveApiClientSeesNothing() {
        createDrucksache(1, true);

        assertThrows(SecurityException.class,
                () -> editor.getPublicDocuments(new ExternalAPIClient(1, false, "hash")));
    }
}