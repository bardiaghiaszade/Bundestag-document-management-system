package main.factory;

import main.docs.*;

import java.util.Objects;
import java.util.List;
import java.time.LocalDateTime;

/**
 * Factory zur zentralen Erstellung von Dokumenten.
 * Statt an verschiedenen Stellen direkt {@code new Drucksache(...)},
 * {@code new Rede(...)} oder {@code new Protokoll(...)} aufzurufen,
 * wird nur diese Klasse verwendet. Sie entscheidet anhand des
 * {@link DocumentType}, welche konkrete Klasse erzeugt wird.
 * Der Aufrufer erhält immer ein {@link Document} und muss den
 * konkreten Typ nicht kennen.
 */
public class DocumentFactory {

    /**
     * Privater Konstruktor: Die Klasse hat nur statische Methoden
     * und soll nicht instanziiert werden.
     */
    private DocumentFactory() {
    }

    /**
     * Erstellt anhand der übergebenen Daten ein Dokument des passenden Typs z.B.
     * DRUCKSACHE: erzeugt eine {@link Drucksache}
     * REDE: erzeugt eine {@link Rede}
     * PROTOKOLL: erzeugt ein {@link Protokoll}
     *
     * @param d die Daten für das neue Dokument
     * @return das neu erstellte Dokument
     * @throws NullPointerException wenn der Typ fehlt oder bei einem Protokoll die Sitzungsnummer fehlt
     */

    public static Document create(DocumentData d) {
        Objects.requireNonNull(d.type(), "type fehlt");
        LocalDateTime now = LocalDateTime.now(); //neues Dokument: erstellt und aktualisiert = jetzt

        //now,now ----> createdAt,updatedAt
        return switch (d.type()) {
            case DRUCKSACHE -> new Drucksache(d.id(), d.title(), d.isPublic(),
                    d.text(), now, now);

            case REDE -> new Rede(d.id(), d.title(), d.isPublic(),
                    d.redner(), d.partei(), d.text(), now, now);

            case PROTOKOLL -> new Protokoll(d.id(), d.title(), d.isPublic(),
                    Objects.requireNonNull(d.sitzungsnummer(), "sitzungsnummer fehlt"),
                    d.text(), now, now, List.of()); //neues Protokoll: noch keine Reden
        };
    }
}