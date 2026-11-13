package main.storage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import main.docs.Document;
import main.docs.DocumentType;
import main.docs.Drucksache;
import main.docs.Protokoll;
import main.docs.Rede;
import main.logs.ChangeLogEntry;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Speichert Dokumente und Änderungs-Log als JSON-Dateien.
 * Beim Hinzufügen eines neuen Dokumenttyps muss man hier toJson und
 * loadAllDocuments ergänzen.
 */
public class JsonDocumentStorage implements DocumentStorage {

    private final Path documentsFile;
    private final Path changeLogFile;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())                      //damit LocalDateTime funktioniert
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)  //Datum als Text statt als Zahlen
            .enable(SerializationFeature.INDENT_OUTPUT)               //Datei gut lesbar formatieren
            .setSerializationInclusion(JsonInclude.Include.NON_NULL); //leere (null) Felder weglassen

    /**
     * constructor
     * @param documentsFile Pfad der Datei für die Dokumente, z.B. data/documents.json
     * @param changeLogFile Pfad der Datei für das Log, z.B. data/changelog.json
     */
    public JsonDocumentStorage(Path documentsFile, Path changeLogFile) {
        this.documentsFile = documentsFile;
        this.changeLogFile = changeLogFile;
    }

    @Override
    public void saveDocument(Document document) {
        List<DocumentJson> list = read(documentsFile, new TypeReference<List<DocumentJson>>() {});
        list.removeIf(j -> j.id() == document.getId()); //altes Dokument mit gleicher id ersetzen
        list.add(toJson(document));
        write(documentsFile, list);
    }

    @Override
    public List<Document> loadAllDocuments() {
        List<DocumentJson> list = read(documentsFile, new TypeReference<List<DocumentJson>>() {});
        List<Document> documents = new ArrayList<>();
        Map<Integer, Rede> reden = new HashMap<>();

        //1) erst Drucksachen und Reden, weil Protokolle die Reden brauchen !!!
        for (DocumentJson j : list) {
            if (j.type() == DocumentType.DRUCKSACHE) {
                documents.add(new Drucksache(j.id(), j.title(), j.publicAccess(),
                        j.text(), j.createdAt(), j.updatedAt()));
            } else if (j.type() == DocumentType.REDE) {
                Rede rede = new Rede(j.id(), j.title(), j.publicAccess(),
                        j.redner(), j.partei(), j.text(), j.createdAt(), j.updatedAt());
                reden.put(rede.getId(), rede);
                documents.add(rede);
            }
        }

        //2) dann die Protokolle, mit den schon geladenen Reden (smart huh? :D )
        for (DocumentJson j : list) {
            if (j.type() == DocumentType.PROTOKOLL) {
                List<Rede> redeListe = new ArrayList<>();
                for (int redeId : j.redeIds()) {
                    redeListe.add(Objects.requireNonNull(reden.get(redeId), "Rede " + redeId + " fehlt"));
                }
                documents.add(new Protokoll(j.id(), j.title(), j.publicAccess(),
                        j.sitzungsnummer(), j.text(), j.createdAt(), j.updatedAt(), redeListe));
            }
        }
        return documents;
    }

    @Override
    public void appendLogEntry(ChangeLogEntry entry) {
        List<ChangeLogEntry> entries = loadLogEntries();
        entries.add(entry);
        write(changeLogFile, entries);
    }

    @Override
    public List<ChangeLogEntry> loadLogEntries() {
        return read(changeLogFile, new TypeReference<List<ChangeLogEntry>>() {});
    }

    /**
     * Wandelt ein Dokument in das JSON-Format um
     * @param d das Dokument
     * @return das Dokument im JSON-Format
     */
    private static DocumentJson toJson(Document d) {
        if (d instanceof Drucksache) {
            return new DocumentJson(DocumentType.DRUCKSACHE, d.getId(), d.getTitle(), d.isPublic(),
                    d.getCreatedAt(), d.getUpdatedAt(), d.getText(), null, null, null, null);
        }
        if (d instanceof Rede r) {
            return new DocumentJson(DocumentType.REDE, d.getId(), d.getTitle(), d.isPublic(),
                    d.getCreatedAt(), d.getUpdatedAt(), d.getText(), r.getRedner(), r.getPartei(), null, null);
        }
        if (d instanceof Protokoll p) {
            List<Integer> redeIds = p.getRedeListe().stream().map(Document::getId).toList();
            return new DocumentJson(DocumentType.PROTOKOLL, d.getId(), d.getTitle(), d.isPublic(),
                    d.getCreatedAt(), d.getUpdatedAt(), d.getText(), null, null, p.getSitzungsnummer(), redeIds);
        }
        throw new IllegalArgumentException("Unbekannter Dokumenttyp: " + d.getClass().getSimpleName());
    }

    /**
     * Liest eine JSON-Liste aus einer Datei (fehlt die Datei oder ist sie leer: leere Liste)
     * @param file die Datei
     * @param type der Listentyp
     * @return die gelesene (veränderbare) Liste
     */
    private <T> List<T> read(Path file, TypeReference<List<T>> type) {
        try {
            if (!Files.exists(file) || Files.size(file) == 0) {
                return new ArrayList<>();
            }
            return mapper.readValue(file.toFile(), type);
        } catch (IOException e) {
            throw new UncheckedIOException("Datei konnte nicht gelesen werden: " + file, e);
        }
    }

    /**
     * Schreibt Daten als JSON in eine Datei (der Ordner wird angelegt, falls er fehlt)
     * @param file die Datei
     * @param data die Daten
     */
    private void write(Path file, Object data) {
        try {
            Files.createDirectories(file.toAbsolutePath().getParent());
            mapper.writeValue(file.toFile(), data);
        } catch (IOException e) {
            throw new UncheckedIOException("Datei konnte nicht geschrieben werden: " + file, e);
        }
    }
}