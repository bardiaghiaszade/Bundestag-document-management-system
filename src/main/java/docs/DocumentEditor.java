package docs;

import factory.DocumentData;
import factory.DocumentFactory;
import logs.ChangeLog;
import logs.ChangeLogEntry;
import logs.ChangeType;
import users.human.HumanUsers;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Der Editor ist der einzige Weg, ein Dokument zu erstellen oder zu ändern.
 * Bei jeder Aktion macht er drei Dinge:
 * 1) die Berechtigung des Benutzers prüfen,
 * 2) die Änderung am Dokument durchführen,
 * 3) die Änderung im {@link ChangeLog} festhalten.
 * Weil alle Methoden mit {@link Document} arbeiten, funktioniert der Editor
 * für jeden Dokumenttyp, auch für neue Typen, die später dazukommen.
 * Er liegt im package docs, weil die SETTER der Dokumente nur innerhalb
 * dieses packages erreichbar sind(weil die kein <public> haben und deswegen private-package sind.
 */
public class DocumentEditor {

    private final ChangeLog changeLog;

    /**
     * constructor
     * @param changeLog das Log, in das alle Änderungen geschrieben werden
     */
    public DocumentEditor(ChangeLog changeLog) {
        this.changeLog = Objects.requireNonNull(changeLog, "changeLog fehlt");
    }

    /**
     * Ein neues Dokument erstellen (mithilfe der {@link DocumentFactory}) und im Log festhalten.
     * @param by Benutzer, der das Dokument erstellt
     * @param data Daten für das neue Dokument
     * @return das neue Dokument
     * @throws SecurityException wenn der Benutzer kein Recht hat
     */
    public Document createDocument(HumanUsers by, DocumentData data) {
        pruefeBerechtigung(by);
        Document doc = DocumentFactory.create(data);
        changeLog.add(new ChangeLogEntry(doc.getId(), by.getId(), doc.getCreatedAt(),
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
        return changeLog.getHistory(documentId);
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
     * Schreibt einen Eintrag ins Log und setzt updatedAt des Dokuments auf denselben Zeitpunkt.
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
        changeLog.add(new ChangeLogEntry(doc.getId(), by.getId(), now,
                type, field, oldValue, newValue));
        doc.setUpdatedAt(now);
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