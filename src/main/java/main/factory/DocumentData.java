package main.factory;

import main.docs.DocumentType;

/**
 * Datenträger für die Erstellung eines Dokuments.
 * Diese Klasse enthält alle Angaben, die für die Erstellung eines der
 * Dokumenttypen (Drucksache, Rede, Protokoll, oder später neuen Dokumenttypen) benötigt werden könnten.
 * Sie enthält keine eigene Logik; sie wird nur an die {@link DocumentFactory} übergeben
 * Felder, die für den gewählten Typ nicht relevant sind, bleiben {@code null}.
 * @param type           Typ des zu erstellenden Dokuments (Drucksache, Rede, Protokoll oder später neuen Dokumenttypen)
 * @param id             id
 * @param title          titel
 * @param isPublic       ob man das z.B. als API Client sehen kann
 * @param redner         Ein benutzer aus Abgeordneter Klasse
 * @param partei         Partei
 * @param text           Inhalt
 * @param sitzungsnummer die Nummer von der Sitzung
 */
public record DocumentData(
        DocumentType type,
        int id,
        String title,
        boolean isPublic,
        String redner,
        String partei,
        String text,
        Integer sitzungsnummer
) {}