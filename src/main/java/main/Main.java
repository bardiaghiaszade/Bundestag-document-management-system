package main;

import main.docs.Document;
import main.docs.DocumentEditor;
import main.docs.DocumentType;
import main.docs.Protokoll;
import main.docs.Rede;
import main.factory.DocumentData;
import main.storage.DocumentStorage;
import main.storage.JsonDocumentStorage;
import main.users.client.ExternalAPIClient;
import main.users.human.Abgeordneter;
import main.users.human.DokumentBearbeiter;
import main.users.human.LogsBearbeiter;

import java.nio.file.Path;

/**
 * Demo des Bundestags-Dokumentenmanagementsystems.
 * Beim ersten Start werden Testdaten angelegt (data/documents.json und data/changelog.json).
 * Danach werden die Dateien nur noch gelesen und geändert.
 */
public class Main {

    public static void main(String[] args) {
        DocumentStorage storage = new JsonDocumentStorage(
                Path.of("data/documents.json"), Path.of("data/changelog.json"));
        DocumentEditor editor = new DocumentEditor(storage);

        DokumentBearbeiter anna = new DokumentBearbeiter(1, "Anna", "Schmidt");
        LogsBearbeiter lars = new LogsBearbeiter(2, "Lars", "Meier");
        Abgeordneter max = new Abgeordneter(3, "Max", "Mustermann", "Partei B");
        ExternalAPIClient apiClient = new ExternalAPIClient(1, true, "hash123");

        //1) Testdaten anlegen (nur beim ersten Start, wenn es noch keine Dokumente gibt)
        if (storage.loadAllDocuments().isEmpty()) {
            editor.createDocument(anna, new DocumentData(DocumentType.DRUCKSACHE, 1,
                    "Antrag zum Klimaschutz", true, null, null,
                    "Der Bundestag möge beschließen, den Ausbau erneuerbarer Energien zu beschleunigen.", null));
            editor.createDocument(anna, new DocumentData(DocumentType.DRUCKSACHE, 2,
                    "Gesetzentwurf zur Digitalisierung der Verwaltung", false, null, null,
                    "Behördengänge sollen künftig vollständig online möglich sein.", null));
            Rede rede1 = (Rede) editor.createDocument(anna, new DocumentData(DocumentType.REDE, 3,
                    "Rede zum Klimaschutz", true, "Anna Beispiel", "Partei A",
                    "Sehr geehrte Damen und Herren, der Klimaschutz duldet keinen Aufschub.", null));
            Rede rede2 = (Rede) editor.createDocument(anna, new DocumentData(DocumentType.REDE, 4,
                    "Rede zur Digitalisierung", true, "Max Mustermann", "Partei B",
                    "Die Digitalisierung der Verwaltung ist überfällig.", null));
            Protokoll protokoll = (Protokoll) editor.createDocument(anna, new DocumentData(DocumentType.PROTOKOLL, 5,
                    "Plenarprotokoll Sitzung 57", true, null, null,
                    "Tagesordnung: Klimaschutz, Digitalisierung der Verwaltung", 57));
            editor.addRede(protokoll, rede1, anna);
            editor.addRede(protokoll, rede2, anna);
        }

        //2) Öffentlichkeit: der API Client sieht nur Dokumente mit isPublic == true
        System.out.println("--- Öffentliche Dokumente (API Client) ---");
        editor.getPublicDocuments(apiClient).forEach(d -> System.out.println(d.getId() + " | " + d.getTitle()));

        //3) Dokument 1 aus der Datei laden und ändern (bei jedem Start wechselt die Sichtbarkeit)
        Document doc = storage.loadAllDocuments().stream()
                .filter(d -> d.getId() == 1).findFirst().orElseThrow();
        editor.changeVisibility(doc, anna, !doc.isPublic());

        //4) Ein Abgeordneter darf keine Dokumente ändern
        try {
            editor.changeText(doc, max, "Unerlaubte Änderung");
        } catch (SecurityException e) {
            System.out.println("Abgelehnt: " + e.getMessage());
        }

        //5) Historie von Dokument 1 (nur für Benutzer mit Log-Berechtigung)
        System.out.println("--- Historie von Dokument 1 ---");
        editor.getHistory(1, lars).forEach(System.out::println);
    }
}