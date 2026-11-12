# Project Walkthrough

This document explains the Bundestag Document Management System step by
step. It is written for readers who are new to the project and want to
understand not only what the program does, but also how its classes work
together.

You do not need to understand every Java concept before reading this
guide. Start with the big picture, then follow the execution flow and
explore the packages one by one.

## 1. What Is This Project?

The Bundestag Document Management System is a small Java console
application that demonstrates how parliamentary documents can be
created, edited, stored, and tracked.

The project supports three document types:

-   **Drucksache** -- a parliamentary printed matter, such as an
    application or a draft bill.
-   **Rede** -- a speech given by a speaker.
-   **Protokoll** -- a record of a parliamentary session. It can contain
    several speeches.

The application also demonstrates:

-   Different user roles and permissions.
-   Public and non-public documents.
-   Saving and loading documents using JSON files.
-   Keeping a history of changes.
-   Creating document objects through a Factory.
-   Testing important behavior with JUnit.

**Important:** This is currently a console-based demonstration. It is
not a complete web application. The external API client is simulated
inside the program; there is no running HTTP server or real API
authentication flow. Human users are also created in memory in `Main`
and are not stored in the JSON files.

## 2. The Big Picture

Before looking at individual classes, it helps to understand the main
parts of the application.

``` text
                    Main.java
                       |
                       v
                 DocumentEditor
                       |
          +------------+------------+
          |            |            |
          v            v            v
      Documents     Permissions   Change Log
          |                         |
          v                         v
    DocumentStorage          ChangeLogEntry
          |
          v
   JsonDocumentStorage
          |
      +---+---+
      |       |
      v       v
documents.json  changelog.json
```

Here is what each part does:

1.  `Main` starts the demonstration and creates the required objects.
2.  `DocumentEditor` is the central class for creating and changing
    documents.
3.  User classes describe who is performing an action and what that user
    is allowed to do.
4.  Document classes represent the different kinds of parliamentary
    documents.
5.  `DocumentStorage` defines how documents and log entries are stored.
6.  `JsonDocumentStorage` implements that storage using JSON files.
7.  `ChangeLogEntry` describes one recorded change.

The most important idea is that `Main` does not directly change document
fields. It asks `DocumentEditor` to perform an operation. The editor
checks permissions, applies the change, records it, and saves the
document.

## 3. Project Structure

The relevant project files are organized like this:

``` text
Bundestag-document-management-system/
│
├── data/
│   ├── documents.json
│   └── changelog.json
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── main/
│   │           ├── Main.java
│   │           │
│   │           ├── docs/
│   │           │   ├── Document.java
│   │           │   ├── DocumentEditor.java
│   │           │   ├── DocumentType.java
│   │           │   ├── Drucksache.java
│   │           │   ├── Rede.java
│   │           │   └── Protokoll.java
│   │           │
│   │           ├── factory/
│   │           │   ├── DocumentData.java
│   │           │   └── DocumentFactory.java
│   │           │
│   │           ├── logs/
│   │           │   ├── ChangeLogEntry.java
│   │           │   └── ChangeType.java
│   │           │
│   │           ├── storage/
│   │           │   ├── DocumentJson.java
│   │           │   ├── DocumentStorage.java
│   │           │   └── JsonDocumentStorage.java
│   │           │
│   │           └── users/
│   │               ├── Berechtigung.java
│   │               ├── client/
│   │               │   └── ExternalAPIClient.java
│   │               └── human/
│   │                   ├── Abgeordneter.java
│   │                   ├── Admin.java
│   │                   ├── DokumentBearbeiter.java
│   │                   ├── HumanUsers.java
│   │                   └── LogsBearbeiter.java
│   │
│   └── test/
│       └── java/
│           ├── docs/
│           │   └── DocumentEditorTest.java
│           └── storage/
│               └── JsonDocumentStorageTest.java
│
└── pom.xml
```

### What are the main folders for?

  Folder       Responsibility
  ------------ ------------------------------------------------------
  `docs`       Document classes and the logic for editing documents
  `factory`    Creates the correct document subtype
  `logs`       Represents recorded changes and their types
  `storage`    Defines and implements JSON persistence
  `users`      Defines user types and permission rules
  `data`       Contains the JSON files used by the application
  `src/test`   Contains automated tests

## 4. The Document Classes

All document types share some common information. For example, every
document has an ID, a title, text, a creation date, an update date, and
a public/private status.

Instead of writing these fields again in every document class, the
project uses an abstract base class called `Document`.

### 4.1 `Document` -- The Base Class

`Document` is an abstract class. This means that the application uses it
as a common parent, but does not create a plain `Document` object
directly.

It contains the shared fields:

-   `id` -- identifies the document.
-   `title` -- the document title.
-   `createdAt` -- when the document was created.
-   `updatedAt` -- when it was last changed.
-   `isPublic` -- whether an external API client may see it.
-   `text` -- the document content.

The fields are private. Other classes read them through getters.

The ID and creation date are final, so they cannot be changed after
construction. The editor changes editable fields through package-private
setter methods. These setters are not public, which helps prevent other
parts of the application from changing a document directly.

The class also overrides `equals()` and `hashCode()`. Two documents are
considered equal when they have the same concrete document type and the
same ID. This is useful when documents are loaded again from JSON:
loading creates new Java objects, but the application can still
recognize a document by its type and ID.

### 4.2 `Drucksache`

`Drucksache` extends `Document`.

It does not need many additional fields because it uses the common
information from `Document`. It represents a parliamentary printed
matter, for example an application or a draft bill.

### 4.3 `Rede`

`Rede` also extends `Document`.

It adds two fields:

-   `redner` -- the speaker's name.
-   `partei` -- the speaker's political party.

These fields are final in the current implementation. The title, text,
and visibility are inherited from `Document`.

### 4.4 `Protokoll`

`Protokoll` extends `Document` and adds:

-   `sitzungsnummer` -- the number of the parliamentary session.
-   `redeListe` -- a list of speeches belonging to the session.

A protocol can contain several `Rede` objects.

The list is copied when the protocol is created. Its public getter
returns an unmodifiable view, so callers cannot change the list
directly. Adding or removing a speech is done through methods that are
accessible inside the `docs` package and are called by `DocumentEditor`.

This keeps changes to the protocol under the editor's control.

### 4.5 `DocumentType`

`DocumentType` is an enum that describes the supported document types:

-   `DRUCKSACHE`
-   `REDE`
-   `PROTOKOLL`

The enum is used by the factory and the JSON storage to identify which
concrete class should be created.

## 5. Creating Documents: The Factory

The project uses the Factory pattern to centralize document creation.

There are two related classes:

### 5.1 `DocumentData`

`DocumentData` is a Java record. It carries the information needed to
create a document, such as its type, ID, title, visibility, text, and
type-specific fields.

It is a data holder. It does not contain the main creation logic.

Some fields are only relevant for one document type. For example,
`redner` and `partei` are relevant to a speech, while `sitzungsnummer`
is relevant to a protocol.

### 5.2 `DocumentFactory`

`DocumentFactory.create()` receives a `DocumentData` object and checks
its `DocumentType`.

It then creates the matching class:

  Document type   Object created
  --------------- ----------------
  `DRUCKSACHE`    `Drucksache`
  `REDE`          `Rede`
  `PROTOKOLL`     `Protokoll`

The method returns the common type `Document`.

Why is this useful?

Without a factory, different parts of the application would need to know
how to construct every document subtype. With the factory, the caller
provides the data and the type, and the factory handles the correct
constructor.

For a new document type, the factory would also need to be updated.

## 6. Users and Permissions

The project has different user roles. Each role has different
permissions.

### 6.1 `Berechtigung` -- The Permission Interface

`Berechtigung` defines three permission methods:

-   `darfBenutzerVerwalten()` -- may manage users.
-   `darfDokumenteBearbeiten()` -- may edit documents.
-   `darfLogsBearbeiten()` -- may access the change history.

The interface describes what permission checks a user type must provide.
Each user class implements these methods.

### 6.2 `HumanUsers` -- The Base Class for Human Users

`HumanUsers` is an abstract class that implements `Berechtigung`.

It contains shared user information:

-   ID
-   First name
-   Last name
-   Party, when applicable

It has overloaded constructors: one constructor accepts a party and the
other does not. This allows the subclasses to use the constructor that
fits their role.

The class provides common getters. The party can be changed, while the
ID and names are final.

### 6.3 Human User Roles

The current permission rules are:

  User role                Manage users   Edit documents   Access logs
  ---------------------- -------------- ---------------- -------------
  `Admin`                           Yes               No            No
  `DokumentBearbeiter`               No              Yes            No
  `LogsBearbeiter`                   No               No           Yes
  `Abgeordneter`                     No               No            No

These rules are implemented by overriding the methods from
`Berechtigung`.

For example, `DokumentBearbeiter.darfDokumenteBearbeiten()` returns
`true`, while the other permission methods return `false`.

**Note:** The `Admin` class currently has permission to manage users,
but the project does not yet implement a user-management workflow.

### 6.4 `ExternalAPIClient`

`ExternalAPIClient` represents an external client that wants to read
public documents.

It has an ID, an active status, and an API key hash field. In the
current demo, the client is created directly in `Main`.

The actual public-document check is performed by
`DocumentEditor.getPublicDocuments()`:

1.  The client must not be `null`.
2.  The client must be active.
3.  Only documents where `isPublic` is `true` are returned.

The API key hash is stored in the client object, but the current demo
does not implement real API key validation or an HTTP API.

## 7. `DocumentEditor` -- The Central Business Logic

`DocumentEditor` is one of the most important classes in the project.

It is responsible for document operations. Other classes should use it
instead of directly changing document fields.

The editor receives a `DocumentStorage` object through its constructor.
This means it does not need to know the exact details of how data is
stored.

### 7.1 Creating a Document

The `createDocument()` method follows this process:

1.  Check whether the user is allowed to edit documents.
2.  Check whether a document with the same ID already exists.
3.  Ask `DocumentFactory` to create the correct document object.
4.  Save the new document.
5.  Add a `CREATED` entry to the change log.
6.  Return the created document.

A duplicate ID is rejected to avoid accidentally replacing an existing
document.

### 7.2 Changing a Title

`changeTitle()`:

1.  Checks the user's document-editing permission.
2.  Rejects an empty title.
3.  Compares the old title with the new title.
4.  Does nothing if the title has not changed.
5.  Otherwise, updates the title.
6.  Records the change and saves the document.

This means an unchanged value does not create an unnecessary log entry.

### 7.3 Changing Document Text

`changeText()` follows a similar process:

1.  Check permission.
2.  Make sure the new text is not `null`.
3.  Compare the old and new text.
4.  Return without logging if they are equal.
5.  Otherwise, update the text, record the change, and save the
    document.

### 7.4 Changing Visibility

`changeVisibility()` changes whether a document is public.

-   `true` means an active external API client may see the document.
-   `false` means the document is not returned by the public-document
    method.

The editor records the old and new visibility values in the change log.

### 7.5 Adding and Removing Speeches

A protocol can contain several speeches.

`addRede()` checks the user's permission and makes sure the speech is
not already in the protocol. It then adds the speech and records a
`REDE_ADDED` entry.

`removeRede()` checks permission and verifies that the speech is in the
protocol. It then removes the speech and records a `REDE_REMOVED` entry.

### 7.6 Reading the Change History

`getHistory()` returns the change-log entries for one document ID.

Only a user whose `darfLogsBearbeiten()` method returns `true` can
access this history. In the current roles, that is `LogsBearbeiter`.

### 7.7 The Shared Change Process

For normal document modifications, the private `logChange()` helper
performs three important tasks:

1.  Sets the document's `updatedAt` value to the current time.
2.  Appends a change entry to `changelog.json`.
3.  Saves the updated document to `documents.json`.

This is why the editor is central: it keeps permission checks, document
updates, logging, and persistence together.

## 8. Change Logging

The `logs` package contains two types.

### 8.1 `ChangeType`

`ChangeType` is an enum that lists the types of changes currently
supported:

-   `CREATED`
-   `TITLE_CHANGED`
-   `TEXT_CHANGED`
-   `VISIBILITY_CHANGED`
-   `REDE_ADDED`
-   `REDE_REMOVED`

Using an enum avoids relying on arbitrary text values for change types.

### 8.2 `ChangeLogEntry`

`ChangeLogEntry` is a Java record. Each entry stores:

-   `documentId` -- which document was changed.
-   `userId` -- who performed the action.
-   `timestamp` -- when it happened.
-   `type` -- what kind of change happened.
-   `field` -- which field or relationship was changed.
-   `oldValue` -- the previous value.
-   `newValue` -- the new value.

For example, a title change can record the old title and the new title.

The record is immutable after it is created. This is useful for a
history entry because an old log entry should not be edited later.

## 9. Saving Data: The Storage Layer

The project separates business logic from file storage.

This is done with an interface and an implementation.

### 9.1 `DocumentStorage`

`DocumentStorage` is an interface. It defines the operations that a
storage implementation must provide, such as:

-   Saving a document.
-   Loading all documents.
-   Appending a change-log entry.
-   Loading change-log entries.

The editor depends on this interface, not directly on JSON file
operations.

This is an example of programming against an abstraction. In the future,
another storage implementation could be created without rewriting the
main document-editing logic.

### 9.2 `JsonDocumentStorage`

`JsonDocumentStorage` implements `DocumentStorage` and uses Jackson to
read and write JSON files.

It receives two file paths:

-   `data/documents.json`
-   `data/changelog.json`

When a document is saved, the storage class reads the current list,
removes an entry with the same ID, adds the updated document, and writes
the list back to the file.

When a log entry is appended, it reads the current log list, adds the
new entry, and writes the list back.

If a file does not exist or is empty, the read helper returns an empty
list. When writing, the required parent directory is created if
necessary.

### 9.3 Why Is There a `DocumentJson` Record?

The Java document classes use inheritance. JSON storage, however, needs
a clear format that can describe all document types.

`DocumentJson` is a record used as the common JSON representation.

It contains shared fields such as type, ID, title, visibility, dates,
and text. It also contains optional fields for the specific document
types:

-   `redner` and `partei` for a `Rede`.
-   `sitzungsnummer` and `redeIds` for a `Protokoll`.

Fields that do not apply to a document type are `null`. Jackson is
configured to omit `null` fields when writing JSON.

### 9.4 How Are Protocols and Speeches Loaded?

A protocol contains references to speeches. The JSON file stores the
speech IDs in `redeIds`, rather than storing complete speech objects
inside the protocol.

Loading therefore happens in two passes:

1.  Load all `Drucksache` and `Rede` objects. Store the loaded speeches
    in a map using their IDs.
2.  Load the `Protokoll` objects. For each ID in `redeIds`, find the
    matching `Rede` object in the map and add it to the protocol.

This order matters because the speeches must exist in memory before the
protocols can refer to them.

### 9.5 What Does Jackson Do Here?

Jackson is the library used to convert Java objects to JSON and JSON
back to Java objects.

The project also registers `JavaTimeModule`, which allows Jackson to
handle `LocalDateTime`.

Dates are written as readable text instead of numeric timestamps. The
JSON output is indented to make it easier for a person to read.

## 10. What Happens When the Program Starts?

The entry point is `Main.java`.

The `main()` method demonstrates the main features in a fixed sequence.

### Step 1: Create the Storage

`Main` creates a `JsonDocumentStorage` object with the paths to the two
JSON files.

It then creates a `DocumentEditor` and passes the storage object to it.

``` java
DocumentStorage storage = new JsonDocumentStorage(
    Path.of("data/documents.json"),
    Path.of("data/changelog.json")
);

DocumentEditor editor = new DocumentEditor(storage);
```

The editor can now load and save data through the storage interface.

### Step 2: Create Demo Users

The program creates example users:

-   Anna Schmidt -- a `DokumentBearbeiter`.
-   Lars Meier -- a `LogsBearbeiter`.
-   Max Mustermann -- an `Abgeordneter`.
-   An active `ExternalAPIClient`.

These objects are created in memory every time the program starts. They
are not loaded from or saved to JSON.

### Step 3: Create Example Documents on the First Run

The program checks whether the storage contains any documents.

If the list is empty, Anna creates five example documents through
`DocumentEditor`:

1.  A public `Drucksache` about climate protection.
2.  A non-public `Drucksache` about digital administration.
3.  A public `Rede` about climate protection.
4.  A public `Rede` about digitalization.
5.  A public `Protokoll` for session 57.

The two speeches are then added to the protocol.

If documents already exist, this initialization block is skipped. The
program does not recreate the example data on every run.

### Step 4: Display Public Documents

The program calls `getPublicDocuments()` with the active API client.

Only documents with `isPublic == true` are printed.

### Step 5: Change the Visibility of Document 1

The program loads document ID `1` from storage.

It then changes its visibility to the opposite of its current value:

``` java
editor.changeVisibility(doc, anna, !doc.isPublic());
```

This means the visibility switches each time `Main` is run.

### Step 6: Demonstrate a Permission Denial

The program asks Max, an `Abgeordneter`, to change the document text.

Max does not have document-editing permission. The editor therefore
throws a `SecurityException`.

`Main` catches the exception and prints a message explaining that the
action was rejected.

### Step 7: Display the Change History

Finally, the program asks for the history of document ID `1`.

It uses Lars, a `LogsBearbeiter`, because this role has permission to
access logs.

The history entries are printed to the console.

## 11. Understanding the Two JSON Files

The `data` directory contains the application's persisted data.

### `documents.json`

This file stores the documents.

Each document has a type, so the application knows whether it should be
loaded as a `Drucksache`, `Rede`, or `Protokoll`.

A protocol stores the IDs of its speeches in `redeIds`. The speeches
themselves are stored as separate document entries.

### `changelog.json`

This file stores the history of changes.

Each entry identifies the document, the user, the time, the change type,
and the old and new values where applicable.

The two files have different responsibilities:

-   `documents.json` stores the current state.
-   `changelog.json` stores the history of changes.

The application persists documents and log entries, but it does not
currently persist human user accounts.

## 12. Automated Tests

The project uses JUnit 5 for automated tests.

The tests are located in `src/test/java`.

### 12.1 `DocumentEditorTest`

This test class checks the behavior of the editor, including:

-   A document change is saved and logged.
-   An unchanged value does not create a new log entry.
-   A user without permission cannot change a document.
-   Duplicate document IDs are rejected.
-   Speeches remain connected to a protocol after reloading.
-   Only a user with log permission can read the history.
-   An API client sees only public documents.
-   An inactive API client cannot see public documents.

### 12.2 `JsonDocumentStorageTest`

This test class checks the JSON storage behavior, including:

-   Documents and log entries can be loaded after saving.
-   Saving a document with an existing ID replaces the previous stored
    version.

### 12.3 Why Do the Tests Use Temporary Directories?

The tests use JUnit's `@TempDir` support to create temporary directories
and JSON files.

This keeps test data separate from the project's normal `data`
directory. Tests can therefore run without changing the example data
used by the console application.

## 13. Technologies Used

  Technology   Purpose
  ------------ -----------------------------------------------
  Java 25      Main programming language configured in Maven
  Maven        Project build and dependency management
  Jackson      Reading and writing JSON
  JUnit 5      Automated tests
  JSON         Persistent storage format

The dependencies and build configuration are defined in `pom.xml`.

## 14. How to Build and Test the Project

You need a compatible Java Development Kit and Maven.

Open a terminal in the project root directory, where `pom.xml` is
located.

Run the tests:

``` bash
mvn test
```

Run a clean test build:

``` bash
mvn clean test
```

Create the Maven package:

``` bash
mvn package
```

The `test` command compiles the application and test code and runs the
automated tests. It does not run the console demonstration in `Main`.

To start the demonstration, run the `main.Main` class from your IDE,
with the project root as the working directory so that the relative
`data/...` paths point to the project's data folder.

## 15. Important Design Ideas Used in This Project

This project brings together several common object-oriented programming
and software design ideas.

### Inheritance

`Drucksache`, `Rede`, and `Protokoll` extend the abstract `Document`
class. They reuse common fields and behavior while adding their own
information.

### Interfaces

`Berechtigung` defines permission methods. `DocumentStorage` defines
storage operations. Different classes implement these interfaces.

### Encapsulation

Document fields are private. Changes are controlled through the editor
and package-private setter methods. The protocol's speech list is
exposed as an unmodifiable list.

### Records and Enums

Records such as `DocumentData`, `DocumentJson`, and `ChangeLogEntry` are
used to represent data. Enums such as `DocumentType` and `ChangeType`
provide a fixed set of possible values.

### Factory Pattern

`DocumentFactory` decides which document class to instantiate based on
the selected document type.

### Separation of Responsibilities

The project gives different tasks to different classes:

-   `Main` demonstrates the application.
-   `DocumentEditor` handles document operations and permission checks.
-   `DocumentFactory` creates document objects.
-   `JsonDocumentStorage` handles JSON persistence.
-   User classes define roles and permissions.
-   Log classes describe changes.

This separation makes the code easier to understand, test, and extend.

## 16. Current Scope and Limitations

It is useful to understand what the current project does and what it
does not do.

The current implementation demonstrates document management, role-based
permission checks, JSON persistence, and change history in a Java
console application.

It does not currently provide:

-   A graphical user interface.
-   A web interface or running HTTP server.
-   A real external API endpoint.
-   Real API key authentication.
-   Persistent user accounts or user management.
-   A database such as MySQL or PostgreSQL.

The `ExternalAPIClient` is a model used to demonstrate access to public
documents. It should not be confused with a fully implemented external
API.

## 17. Suggested Reading Order

If you are new to the codebase, this order is a good way to explore it:

1.  Start with `Main.java` to understand the example program.
2.  Read `Document.java` to understand the common document structure.
3.  Read `Drucksache.java`, `Rede.java`, and `Protokoll.java` to see
    inheritance in practice.
4.  Read `Berechtigung.java` and the user classes to understand
    permissions.
5.  Read `DocumentFactory.java` to understand how document objects are
    created.
6.  Read `DocumentEditor.java` to understand the main application logic.
7.  Read `DocumentStorage.java` and `JsonDocumentStorage.java` to
    understand persistence.
8.  Read the classes in `logs` to understand change tracking.
9.  Finally, explore the tests to see how the expected behavior is
    verified.

Following this order takes you from the basic data model to the complete
execution flow.

------------------------------------------------------------------------

**In short:** `Main` starts the demonstration, `DocumentEditor` controls
document operations, the user classes define permissions, the factory
creates the correct document types, and the storage layer saves the
current documents and their change history as JSON.
