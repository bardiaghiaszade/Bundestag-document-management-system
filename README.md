## Project Documentation (ENGLISH VERSION)

Want to understand how this project works internally?

Read the [Project Walkthrough](PROJECT_WALKTHROUGH.md)
for a detailed, step-by-step explanation of the project
architecture, classes, permissions, data storage, and execution flow.

This guide is written for developers who are new to the project.


## Project Documentation (DEUTSCH VERSION) ----->

# Bundestag Dokumentenmanagementsystem

Ein Java-Projekt zur Verwaltung von Dokumenten des Deutschen Bundestages.

Mit diesem Programm können verschiedene Dokumente erstellt, gespeichert, geändert und angezeigt werden. Außerdem werden alle Änderungen in einer Historie gespeichert. Das Programm prüft auch, welche Benutzer bestimmte Aktionen ausführen dürfen.

Das Projekt wurde mit Java und Maven entwickelt. Die Dokumente und ihre Änderungen werden in JSON-Dateien gespeichert.

---

## Inhaltsverzeichnis

- [Über das Projekt](#über-das-projekt)
- [Funktionen](#funktionen)
- [Dokumenttypen](#dokumenttypen)
- [Benutzer und Rechte](#benutzer-und-rechte)
- [Projektstruktur](#projektstruktur)
- [Wie funktioniert das Programm?](#wie-funktioniert-das-programm)
- [Verwendete Technologien](#verwendete-technologien)
- [Voraussetzungen](#voraussetzungen)
- [Installation](#installation)
- [Programm starten](#programm-starten)
- [Tests ausführen](#tests-ausführen)
- [Datenspeicherung](#datenspeicherung)
- [Wichtige Hinweise](#wichtige-hinweise)

---

## Über das Projekt

Das Bundestag Dokumentenmanagementsystem ist ein einfaches System zur Verwaltung von Dokumenten.

Das Programm unterstützt drei Arten von Dokumenten:

- **Drucksache:** Ein Antrag oder ein offizielles Dokument.
- **Rede:** Eine Rede eines Abgeordneten.
- **Protokoll:** Ein Protokoll einer Sitzung, das mehrere Reden enthalten kann.

Jedes Dokument hat eine eigene ID, einen Titel, einen Text und Informationen zum Erstellungsdatum und zur letzten Änderung.

Außerdem kann festgelegt werden, ob ein Dokument öffentlich ist. Öffentliche Dokumente können von einem aktiven API-Client gelesen werden.

Das Programm speichert alle Dokumente und Änderungen in JSON-Dateien. Dadurch bleiben die Daten auch nach dem Beenden des Programms erhalten.

## Funktionen

Das Projekt bietet folgende Funktionen:

- Neue Dokumente erstellen
- Dokumente nach ihrer ID verwalten
- Titel und Text von Dokumenten ändern
- Dokumente öffentlich oder nicht öffentlich machen
- Reden zu einem Protokoll hinzufügen
- Reden aus einem Protokoll entfernen
- Benutzerrechte prüfen
- Nur öffentliche Dokumente für aktive API-Clients anzeigen
- Änderungen automatisch in einer Historie speichern
- Dokumente und Änderungen in JSON-Dateien speichern
- Dokumente und ihre Verbindungen nach einem Neustart wieder laden
- Automatische Tests mit JUnit durchführen

## Dokumenttypen

Alle Dokumente basieren auf der abstrakten Klasse `Document`.

Die verschiedenen Dokumenttypen erweitern diese Klasse und haben zusätzliche Informationen.

| Klasse | Beschreibung |
|---|---|
| `Document` | Gemeinsame Eigenschaften aller Dokumente |
| `Drucksache` | Ein Antrag oder offizielles Dokument |
| `Rede` | Eine Rede mit Redner und Partei |
| `Protokoll` | Ein Sitzungsprotokoll mit mehreren Reden |

Ein Protokoll kann mehrere Reden enthalten. Die Reden werden als eigene Dokumente gespeichert und über ihre IDs mit dem Protokoll verbunden.

## Benutzer und Rechte

Das System hat verschiedene Benutzerrollen. Jede Rolle hat eigene Rechte.

| Benutzer | Benutzer verwalten | Dokumente bearbeiten | Historie lesen |
|---|---|---|---|
| Admin | Ja | Nein | Nein |
| DokumentBearbeiter | Nein | Ja | Nein |
| LogsBearbeiter | Nein | Nein | Ja |
| Abgeordneter | Nein | Nein | Nein |
| ExternalAPIClient | Nein | Nein | Nein |

Der `ExternalAPIClient` hat eine eigene Funktion: Er kann öffentliche Dokumente lesen, wenn er aktiv ist.

Nicht öffentliche Dokumente werden ihm nicht angezeigt.

Die Rechte werden über das Interface `Berechtigung` festgelegt und in den jeweiligen Klassen umgesetzt.

**Hinweis:** Die Benutzer werden im aktuellen Programm nur während der Ausführung erstellt. Sie werden nicht in JSON-Dateien gespeichert.

## Projektstruktur

Das Projekt ist in mehrere Packages aufgeteilt. Jedes Package hat eine eigene Aufgabe.

```text
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
│   │           │
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
│   │           │   ├── DocumentFactory.java
│   │           │   └── DocumentData.java
│   │           │
│   │           ├── storage/
│   │           │   ├── DocumentStorage.java
│   │           │   ├── DocumentJson.java
│   │           │   └── JsonDocumentStorage.java
│   │           │
│   │           ├── users/
│   │           │   ├── Berechtigung.java
│   │           │   ├── client/
│   │           │   │   └── ExternalAPIClient.java
│   │           │   └── human/
│   │           │       ├── HumanUsers.java
│   │           │       ├── Admin.java
│   │           │       ├── Abgeordneter.java
│   │           │       ├── DokumentBearbeiter.java
│   │           │       └── LogsBearbeiter.java
│   │           │
│   │           └── logs/
│   │               ├── ChangeLogEntry.java
│   │               └── ChangeType.java
│   │
│   └── test/
│       └── java/
│           ├── docs/
│           │   └── DocumentEditorTest.java
│           │
│           └── storage/
│               └── JsonDocumentStorageTest.java
│
├── pom.xml
└── README.md
```

### Aufgaben der einzelnen Packages

**docs**

Enthält die Dokumentklassen und den `DocumentEditor`. Der Editor ist für das Erstellen, Ändern und Lesen von Dokumenten zuständig. Vor Änderungen prüft er die Rechte des Benutzers.

**factory**

Enthält die `DocumentFactory`. Sie erstellt anhand des Dokumenttyps das passende Objekt, zum Beispiel eine Drucksache, eine Rede oder ein Protokoll.

**storage**

Ist für das Speichern und Laden der Daten zuständig.

- `DocumentStorage` definiert die Methoden für die Speicherung.
- `JsonDocumentStorage` führt diese Methoden mit JSON-Dateien aus.
- `DocumentJson` beschreibt, wie ein Dokument in einer JSON-Datei gespeichert wird.

**users**

Enthält die Benutzerklassen und das Interface `Berechtigung`. Hier werden die verschiedenen Benutzerrollen und ihre Rechte festgelegt.

**logs**

Enthält die Klassen für die Änderungshistorie.

- `ChangeLogEntry` speichert die Informationen zu einer Änderung.
- `ChangeType` enthält die möglichen Arten von Änderungen.

**test**

Enthält die automatischen Tests für den Editor und die Speicherung.

## Wie funktioniert das Programm?

Die Klasse `Main.java` ist der Startpunkt des Programms.

Beim Start werden die wichtigsten Objekte erstellt und miteinander verbunden.

Der Ablauf sieht so aus:

1. **Storage erstellen:** Das Programm legt fest, welche JSON-Dateien für Dokumente und Änderungen verwendet werden.
2. **DocumentEditor erstellen:** Der Editor erhält den Storage und kann damit Dokumente speichern und laden.
3. **Benutzer erstellen:** Beispielbenutzer mit verschiedenen Rechten werden angelegt.
4. **Dokumente laden:** Das Programm prüft, ob bereits Dokumente vorhanden sind.
5. **Testdaten erstellen:** Wenn noch keine Dokumente vorhanden sind, werden fünf Beispieldokumente erstellt.
6. **Reden verbinden:** Zwei Reden werden dem Sitzungsprotokoll hinzugefügt.
7. **Öffentliche Dokumente anzeigen:** Ein aktiver API-Client liest alle öffentlichen Dokumente.
8. **Dokument ändern:** Die Sichtbarkeit des Dokuments mit der ID 1 wird geändert.
9. **Rechte prüfen:** Ein Abgeordneter versucht, ein Dokument zu ändern. Da er keine Rechte hat, wird die Aktion abgelehnt.
10. **Historie anzeigen:** Der LogsBearbeiter liest die gespeicherten Änderungen des Dokuments mit der ID 1.

Am Ende werden die Ergebnisse in der Konsole angezeigt.

### Wie werden Änderungen gespeichert?

Wenn ein Benutzer ein Dokument ändert, führt der `DocumentEditor` mehrere Schritte aus:

1. Die Rechte des Benutzers werden geprüft.
2. Die Änderung wird am Dokument durchgeführt.
3. Ein neuer Eintrag für die Änderung wird erstellt.
4. Der Eintrag wird in `changelog.json` gespeichert.
5. Das geänderte Dokument wird in `documents.json` gespeichert.

So kann später nachvollzogen werden, wer ein Dokument geändert hat, wann die Änderung stattgefunden hat und welche Werte vorher und nachher vorhanden waren.

## Verwendete Technologien

| Technologie | Verwendung |
|---|---|
| Java 25 | Programmiersprache |
| Maven | Projektverwaltung und Build |
| Jackson | JSON-Dateien lesen und schreiben |
| JUnit 5 | Automatische Tests |
| Git | Versionsverwaltung |

### Verwendete Java-Konzepte

Im Projekt werden verschiedene Java-Konzepte verwendet:

- Objektorientierte Programmierung (OOP)
- Vererbung (Inheritance)
- Abstrakte Klassen (Abstract Classes)
- Interfaces
- Enums
- Records
- Datenkapselung (Encapsulation)
- Methodenüberschreibung (Method Overriding)
- Factory Pattern
- Collections wie `List` und `Map`
- Exceptions und Fehlerbehandlung
- Java Streams

## Voraussetzungen

Um das Projekt auszuführen, benötigst du:

- Java JDK 21
- Apache Maven
- Eine Java-IDE, zum Beispiel IntelliJ IDEA oder Eclipse

Du kannst die installierten Versionen im Terminal prüfen:

```bash
java -version
mvn -version
```

## Installation

**1. Repository herunterladen**

```bash
git clone <REPOSITORY-URL>
```

Alternativ kannst du das Projekt als ZIP-Datei herunterladen und entpacken.

**2. Projektordner öffnen**

Öffne den Projektordner in deiner IDE, zum Beispiel in IntelliJ IDEA.

**3. Maven-Projekt laden**

Die IDE erkennt die Datei `pom.xml` und lädt die benötigten Bibliotheken automatisch herunter.

Alternativ kannst du im Terminal im Projektordner folgenden Befehl ausführen:

```bash
mvn clean install
```

## Programm starten

Öffne die Datei:

```text
src/main/java/main/Main.java
```

Starte die `main()`-Methode über deine IDE.

Das Programm führt anschließend das Beispiel aus und zeigt die Ergebnisse in der Konsole an.

Beim ersten Start werden die Beispieldaten angelegt, wenn noch keine Dokumente vorhanden sind.

**Wichtig:** Bei jedem weiteren Start wird die Sichtbarkeit des Dokuments mit der ID 1 erneut geändert. Dadurch wechselt der Wert zwischen `true` und `false`.

## Tests ausführen

Für die Tests wird JUnit 5 verwendet.

Die Tests prüfen, ob die wichtigsten Funktionen richtig arbeiten.

Es gibt zwei Testklassen:

### DocumentEditorTest

Diese Tests prüfen unter anderem:

- Dokumente ändern und Änderungen speichern
- Änderungshistorie erstellen
- Keine Log-Einträge bei unveränderten Werten erstellen
- Nicht erlaubte Änderungen verhindern
- Doppelte Dokument-IDs ablehnen
- Reden nach dem erneuten Laden im Protokoll behalten
- Benutzerrechte für die Historie prüfen
- Nur öffentliche Dokumente für API-Clients anzeigen
- Inaktive API-Clients ablehnen

### JsonDocumentStorageTest

Diese Tests prüfen:

- Dokumente und Log-Einträge speichern und erneut laden
- Dokumentdaten und Zeitangaben erhalten
- Reden einem Protokoll richtig zuordnen
- Ein Dokument mit derselben ID durch die neue Version ersetzen

### Alle Tests starten

Öffne das Terminal im Hauptordner des Projekts und führe folgenden Befehl aus:

```bash
mvn test
```

Maven kompiliert das Projekt, startet die JUnit-Tests und zeigt das Ergebnis an.

Für einen neuen Build mit vorherigem Löschen der alten Build-Dateien:

```bash
mvn clean test
```

Die Testberichte werden im Ordner `target/surefire-reports/` gespeichert.

**Hinweis:** Die Tests verwenden temporäre JSON-Dateien. Die eigentlichen Dateien im Ordner `data/` werden dabei nicht verändert.

## Datenspeicherung

Das Programm verwendet zwei JSON-Dateien:

### documents.json

Diese Datei enthält alle gespeicherten Dokumente.

Dazu gehören:

- Dokument-ID
- Dokumenttyp
- Titel
- Text
- Öffentlicher oder nicht öffentlicher Zugriff
- Erstellungsdatum
- Datum der letzten Änderung
- Zusätzliche Informationen zum jeweiligen Dokumenttyp

Bei einer Rede werden zum Beispiel auch der Name des Redners und die Partei gespeichert.

Bei einem Protokoll werden die Sitzungsnummer und die IDs der enthaltenen Reden gespeichert.

### changelog.json

Diese Datei enthält die Änderungshistorie.

Jeder Eintrag enthält:

- ID des geänderten Dokuments
- ID des Benutzers
- Zeitpunkt der Änderung
- Art der Änderung
- Geändertes Feld
- Alter Wert
- Neuer Wert

Die Daten werden beim Speichern aktualisiert und beim nächsten Programmstart wieder geladen.

## Wichtige Hinweise

- Das Projekt ist aktuell ein Java-Konsolenprogramm und keine Webanwendung.
- Der `ExternalAPIClient` zeigt die Funktion für den Zugriff auf öffentliche Dokumente. Ein echter API-Server ist nicht enthalten.
- Benutzer werden aktuell nur während der Programmausführung erstellt und nicht dauerhaft gespeichert.
- Die Beispieldaten werden nur erstellt, wenn noch keine Dokumente vorhanden sind.
- Die JSON-Dateien im Ordner `data/` enthalten die gespeicherten Daten des Programms.

---