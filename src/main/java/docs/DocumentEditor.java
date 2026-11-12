package docs;

import users.client.ExternalAPIClient;
import factory.DocumentData;
import factory.DocumentFactory;
import logs.ChangeLogEntry;
import logs.ChangeType;
import storage.DocumentStorage;
import users.human.HumanUsers;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Der Editor ist der einzige Weg, ein Dokument zu erstellen oder zu ändern.
 * Bei jeder Aktion macht er vier Dinge:
 * 1) die Berechtigung des Benutzers prüfen,
 * 2) die Änderung am Dokument durchführen,
 * 3) die Änderung im Änderungs-Log festhalten (changelog.json),
 * 4) das geänderte Dokument speichern (documents.json).
 * Weil alle Methoden mit {@link Document} arbeiten, funktioniert der Editor
 * für jeden Dokumenttyp, auch für neue Typen, die später dazukommen.
 * Er liegt im package docs, weil die SETTER der Dokumente nur innerhalb
 * dieses packages erreichbar sind(weil die kein <public> haben und deswegen private-package sind.
 */
public class DocumentEditor {

    private final DocumentStorage storage;

    /**
     * constructor
     * @param storage hier werden die Dokumente und das Änderungs-Log gespeichert
     */
    public DocumentEditor(DocumentStorage storage) {
        this.storage = Objects.requireNonNull(storage, "storage fehlt");
    }

    /**
     * Ein neues Dokument erstellen (mithilfe der {@link DocumentFactory}), speichern und im Log festhalten.
     * @param by Benutzer, der das Dokument erstellt
     * @param data Daten für das neue Dokument
     * @return das neue Dokument
     * @throws SecurityException wenn der Benutzer kein Recht hat
     * @throws IllegalArgumentException wenn es die id schon gibt
     */
    public Document createDocument(HumanUsers by, DocumentData data) {
        pruefeBerechtigung(by);
        //eine schon vorhandene id würde das gespeicherte Dokument überschreiben
        if (storage.loadAllDocuments().stream().anyMatch(d -> d.getId() == data.id())) {
            throw new IllegalArgumentException("Dokument mit id " + data.id() + " existiert schon");
        }
        Document doc = DocumentFactory.create(data);
        storage.saveDocument(doc);
        storage.appendLogEntry(new ChangeLogEntry(doc.getId(), by.getId(), doc.getCreatedAt(),
                ChangeType.CREATED, "document", null, doc.getTitle()));
        return doc;
    }

    /**
     * Titel eines Dokuments ändern.
     * @param doc das Dokument
     * @param by Benutzer, der ändert
     * @param newTitle neuer Titel
     * @throws SecurityException        wenn der Benutzer kein Recht hat
     * @throws IllegalArgumentException wenn der Titel leer ist
     */
    public void changeTitle(Document doc, HumanUsers by, String newTitle) {
        pruefeBerechtigung(by);
        if (newTitle == null || newTitle.isBlank()) {
            throw new IllegalArgumentException("titel darf nicht leer sein");
        }
        String oldTitle = doc.getTitle();
        if (oldTitle.equals(newTitle)) {
            return; //nichts geändert, also kein Eintrag im Log
        }
        doc.setTitle(newTitle);
        logChange(doc, by, ChangeType.TITLE_CHANGED, "title", oldTitle, newTitle);
    }

    /**
     * Inhalt eines Dokuments ändern.
     * @param doc das Dokument
     * @param by Benutzer, der ändert
     * @param newText neuer Inhalt
     * @throws SecurityException wenn der Benutzer kein Recht hat
     */
    public void changeText(Document doc, HumanUsers by, String newText) {
        pruefeBerechtigung(by);
        Objects.requireNonNull(newText, "text fehlt");
        String oldText = doc.getText();
        if (newText.equals(oldText)) {
            return; //nichts geändert, also kein Eintrag im Log
        }
        doc.setText(newText);
        logChange(doc, by, ChangeType.TEXT_CHANGED, "text", oldText, newText);
    }

    /**
     * Sichtbarkeit eines Dokuments ändern (öffentlich für API Clients oder nicht).
     * @param doc das Dokument
     * @param by Benutzer, der ändert
     * @param isPublic true, wenn das Dokument öffentlich ist
     * @throws SecurityException wenn der Benutzer kein Recht hat
     */
    public void changeVisibility(Document doc, HumanUsers by, boolean isPublic) {
        pruefeBerechtigung(by);
        if (doc.isPublic() == isPublic) {
            return; //nichts geändert, also kein Eintrag im Log
        }
        doc.setPublic(isPublic);
        logChange(doc, by, ChangeType.VISIBILITY_CHANGED, "isPublic",
                String.valueOf(!isPublic), String.valueOf(isPublic));
    }

    /**
     * Eine Rede in ein Protokoll einfügen.
     * @param protokoll das Protokoll
     * @param rede die neue Rede
     * @param by Benutzer, der ändert
     * @throws SecurityException     wenn der Benutzer kein Recht hat
     * @throws IllegalStateException wenn die Rede schon im Protokoll ist
     */
    public void addRede(Protokoll protokoll, Rede rede, HumanUsers by) {
        pruefeBerechtigung(by);
        Objects.requireNonNull(rede, "rede fehlt");
        if (protokoll.getRedeListe().contains(rede)) {
            throw new IllegalStateException("Rede ist schon im Protokoll");
        }
        protokoll.addRede(rede);
        logChange(protokoll, by, ChangeType.REDE_ADDED, "redeListe",
                null, beschreibe(rede));
    }

    /**
     * Eine Rede aus einem Protokoll entfernen.
     * @param protokoll das Protokoll
     * @param rede die Rede, die entfernt wird
     * @param by Benutzer, der ändert
     * @throws SecurityException        wenn der Benutzer kein Recht hat
     * @throws IllegalArgumentException wenn die Rede nicht im Protokoll ist
     */
    public void removeRede(Protokoll protokoll, Rede rede, HumanUsers by) {
        pruefeBerechtigung(by);
        Objects.requireNonNull(rede, "rede fehlt");
        if (!protokoll.getRedeListe().contains(rede)) {
            throw new IllegalArgumentException("Rede ist nicht im Protokoll");
        }
        protokoll.removeRede(rede);
        logChange(protokoll, by, ChangeType.REDE_REMOVED, "redeListe",
                beschreibe(rede), null);
    }

    /**
     * Historie eines Dokuments lesen.
     * @param documentId id des Dokuments
     * @param by Benutzer, der die Historie sehen möchte
     * @return alle Änderungen dieses Dokuments, älteste zuerst
     * @throws SecurityException wenn der Benutzer keine Logs bearbeiten darf
     */
    public List<ChangeLogEntry> getHistory(int documentId, HumanUsers by) {
        Objects.requireNonNull(by, "benutzer fehlt");
        if (!by.darfLogsBearbeiten()) {
            throw new SecurityException("Keine Berechtigung für Logs");
        }
        return storage.loadLogEntries().stream()
                .filter(e -> e.documentId() == documentId)
                .toList();
    }

    /**
     * Alle öffentlichen Dokumente lesen (für die Öffentlichkeit über die API).
     * Dokumente mit isPublic == false bekommt ein API Client nie zu sehen.
     * @param client der API Client, der lesen möchte
     * @return alle Dokumente mit isPublic == true
     * @throws SecurityException wenn der API Client nicht aktiv ist
     */
    public List<Document> getPublicDocuments(ExternalAPIClient client) {
        Objects.requireNonNull(client, "client fehlt");
        if (!client.getIsActive()) {
            throw new SecurityException("API Client ist nicht aktiv");
        }
        return storage.loadAllDocuments().stream()
                .filter(Document::isPublic)
                .toList();
    }

    //Hilfsmethoden (private, nur für den Editor selbst)

    /**
     * Prüft, ob der Benutzer Dokumente bearbeiten darf.
     * @param by der Benutzer
     * @throws SecurityException wenn der Benutzer kein Recht hat
     */
    private void pruefeBerechtigung(HumanUsers by) {
        Objects.requireNonNull(by, "benutzer fehlt");
        if (!by.darfDokumenteBearbeiten()) {
            throw new SecurityException("Keine Berechtigung zum Bearbeiten von Dokumenten");
        }
    }

    /**
     * Schreibt einen Eintrag ins Log (changelog.json), setzt updatedAt des Dokuments auf denselben
     * Zeitpunkt und speichert das geänderte Dokument (documents.json).
     * @param doc das geänderte Dokument
     * @param by der Benutzer
     * @param type typ
     * @param field Name des Feldes
     * @param oldValue Wert vorher
     * @param newValue Wert nachher
     */
    private void logChange(Document doc, HumanUsers by, ChangeType type,
                           String field, String oldValue, String newValue) {
        LocalDateTime now = LocalDateTime.now();
        doc.setUpdatedAt(now);
        storage.appendLogEntry(new ChangeLogEntry(doc.getId(), by.getId(), now,
                type, field, oldValue, newValue));
        storage.saveDocument(doc); //das geänderte Dokument sofort speichern
    }

    /**
     * Kurzbeschreibung einer Rede für das Log.
     * @param rede die Rede
     * @return Text aus id, Titel und Redner
     */
    private String beschreibe(Rede rede) {
        return "Rede " + rede.getId() + " (" + rede.getTitle() + ", " + rede.getRedner() + ")";
    }
}