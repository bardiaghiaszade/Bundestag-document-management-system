package main.logs;

/**
 * Alle Arten von Änderungen.
 * Beim Bedarf einer neuen Art von Änderung (z.B. für einen neuen Dokumenttyp)
 * muss man das hier hinzufügen und eine passende Methode in docs/DocumentEditor schreiben.
 */
public enum ChangeType {
    CREATED,
    TITLE_CHANGED,
    TEXT_CHANGED,
    VISIBILITY_CHANGED,
    REDE_ADDED,
    REDE_REMOVED
}