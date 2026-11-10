package docs;

/**
 * Alle Dokumenttypen des Systems.
 * Beim Bedarf von Erstellung einen neuen Typ von Dokument muss man das in:
 * 1) ENUM: docs/DocumentType hinzufügen und
 * 2) auch einen neuen switch dafür in CLASS: factory/DocumentFactory erstellen und
 * 3) falls nötig passende Parameter in RECORD: factory/DocumentData hinzufügen
 */
public enum DocumentType {
    DRUCKSACHE,
    PROTOKOLL,
    REDE
}
