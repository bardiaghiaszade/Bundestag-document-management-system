package logs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Das interne Änderungs-Log des Systems.
 * Hier werden alle Änderungen an allen Dokumenten in zeitlicher Reihenfolge gesammelt.
 * Einträge werden nur hinzugefügt, nie geändert oder gelöscht,
 * damit die Historie nachvollziehbar bleibt.
 */
public class ChangeLog {

    // eine finale Liste kann inhaltlich geändert werden(z.B. mit add)!
    private final List<ChangeLogEntry> entries = new ArrayList<>();

    /**
     * Neuen Eintrag am Ende des Logs hinzufügen.
     * Wird vom DocumentEditor aufgerufen, nicht von außen direkt.
     * @param entry der neue Eintrag
     * @throws NullPointerException wenn der Eintrag fehlt
     */
    public void add(ChangeLogEntry entry) {
        entries.add(Objects.requireNonNull(entry, "entry fehlt"));
    }

    /**
     * entries-getter
     * @return alle Einträge (nur lesbar), älteste zuerst
     */
    public List<ChangeLogEntry> getAll() {
        return Collections.unmodifiableList(entries);
    }

    /**
     * Historie eines einzelnen Dokuments
     * @param documentId id des Dokuments
     * @return alle Einträge dieses Dokuments (nur lesbar), älteste zuerst
     */
    public List<ChangeLogEntry> getHistory(int documentId) {
        return entries.stream()
                .filter(e -> e.documentId() == documentId)
                .toList();
    }
}