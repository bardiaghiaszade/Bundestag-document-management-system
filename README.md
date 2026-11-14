# Bundestag Document Management System

Small Java 21 / Maven project that manages Bundestag documents (Drucksache, Rede, Protokoll) in JSON files, with a change log for every edit.

Run: mvn test for the tests, main.Main for a small demo. Data goes to data/documents.json and data/changelog.json.

## Creating a new document

Documents are never created with new. Everything goes through DocumentEditor:
DocumentStorage storage = new JsonDocumentStorage(
        Path.of(“data/documents.json”), Path.of(“data/changelog.json”));
DocumentEditor editor = new DocumentEditor(storage);
DokumentBearbeiter anna = new DokumentBearbeiter(1, “Anna”, “Schmidt”);

Document d = editor.createDocument(anna, new DocumentData(
        DocumentType.DRUCKSACHE, 10, “Title”, true, null, null, “Text”, null));

DocumentData(type, id, title, isPublic, redner, partei, text, sitzungsnummer). Fields that don’t belong to the type stay null:

| Type | Needs | Leave null |
|—|—|—|
| DRUCKSACHE | id, title, isPublic, text | redner, partei, sitzungsnummer |
| REDE | id, title, isPublic, text, redner, partei | sitzungsnummer |
| PROTOKOLL | id, title, isPublic, text, sitzungsnummer | redner, partei |

What createDocument does:
1. Checks that the user may edit documents (only DokumentBearbeiter, otherwise `SecurityException`).
2. Checks that the id is free (otherwise `IllegalArgumentException`).
3. Builds the document with DocumentFactory, saves it and writes a CREATED log entry.

A Protokoll starts empty. Add speeches afterwards with editor.addRede(protokoll, rede, user) (the Rede must exist already). Later changes (`changeTitle`, changeText, changeVisibility, addRede, `removeRede`) all go through the editor too, so each one is checked, saved and logged.

## Classes

docs
- Document (abstract): id, title, text, isPublic, timestamps. Setters are package-private, so only DocumentEditor can change a document.
- Drucksache, Rede, Protokoll: the concrete types. Protokoll holds a list of Rede.
- DocumentType: enum of the types, used by factory and storage.
- DocumentEditor: the only entry point for create/change. Checks permission, changes the document, logs, saves. Uses DocumentFactory and DocumentStorage.

factory
- DocumentFactory: creates the right Document from a DocumentData.
- DocumentData: record with all input fields for a new document.

storage
- DocumentStorage (interface): save/load documents, append/load log entries.
- JsonDocumentStorage: implements it with Jackson, uses DocumentJson as the file format.
- DocumentJson: flat record for one document in JSON (a Protokoll stores only the `redeIds`).

logs
- ChangeLogEntry: one change (document id, user id, time, type, field, old/new value).
- ChangeType: enum of change kinds.

users
- Berechtigung (interface): darfBenutzerVerwalten, darfDokumenteBearbeiten, darfLogsBearbeiten.
- HumanUsers (abstract) with Admin, DokumentBearbeiter, LogsBearbeiter, Abgeordneter.
- ExternalAPIClient: the public API user, only sees documents with isPublic == true.

## To be developed further

- New document types: right now this means changing DocumentType, DocumentData, the switch in DocumentFactory, and toJson / loadAllDocuments in JsonDocumentStorage. Better: let every type create and serialize itself.
- Drucksache only has title and text, fields like number or author are missing.
- Rede.redner is just a String, not linked to an Abgeordneter.
- No deleting of documents, and redner, partei and sitzungsnummer can’t be changed after creation.
- User management (`Admin`) is not implemented yet.
- JSON files are fine for now, a real database would be the next step. The API for the public is also still missing.