Aufgabenbeschreibung -------------->


Der Arbeitskreis MakeBundestagDigital war mit Ihrer bisherigen Arbeit zufrieden und hat einen neuen
Arbeitsauftrag für Sie formuliert, der im Folgenden näher beschrieben wird:
Bundestags-Dokumentenmanagementsystem
Die Dokumentenablage des Deutschen Bundestages ist derzeit nicht ausreichend strukturiert or
ganisiert. Ihre Aufgabe besteht darin, diesen Zustand zu verbessern. Um künftig sämtliche Doku
mente des Deutschen Bundestages systematisch erfassen und verwalten zu können, soll ein Do
kumentenmanagementsystem entwickelt werden. Auf dieses sollen sowohl zahlreiche Benutzer
mit unterschiedlichen Berechtigungen als auch die Öffentlichkeit über eine zukünftig bereitgestellte
API zugreifen können. Da zum jetzigen Zeitpunkt noch nicht feststeht, welche Dokumenttypen
im Detail abgebildet werden müssen, soll die von Ihnen entworfene Architektur möglichst flexibel
und erweiterbar gestaltet sein. Gleichzeitig sollen zukünftige Anpassungen ohne unverhältnismä
ßig hohen Entwicklungsaufwand umgesetzt werden können. Darüber hinaus möchten wir auf eine
dateibasierte Dokumentenablage verzichten. Das bedeutet, dass keine PDFs oder andere Dateifor
mate hochgeladen werden sollen. Stattdessen müssen sämtliche Informationen innerhalb geeigneter
Datenstrukturen repräsentiert werden. Die folgenden Dokumenttypen sollen zunächst abgebildet
werden:
• Drucksache
• Protokoll
• Rede
Bitte entwerfen Sie die dafür erforderlichen Felder und Datenstrukturen, sodass ein flexibler Editor
zur Verwaltung unserer Dokumente realisiert werden kann. Von besonderer Bedeutung ist für uns
ein internes Änderungs-Log, das sämtliche Änderungen nachvollziehbar dokumentiert und deren
Historie abbildet. Die Anwendung soll unter Verwendung des Factory-Design-Patterns realisiert
werden.


Implementieren Sie die Applikation „Bundestags-Dokumentenmanagementsystem“ auf Grundlage der be
schriebenen Anforderungen des Arbeitskreises des Deutschen Bundestages. Verwenden Sie bei Ihrer Imple
mentierung sowohl Interfaces als auch abstrakte Klassen. Die von Ihnen zu entwickelnden Datenstrukturen
müssen die genannten Dokumenttypen sowie die daraus entstehenden Zusammenhänge und Strukturen
vollständig abbilden. Um Ihrem Start-up einen Wettbewerbsvorteil zu verschaffen und den Zuschlag zu
sichern, sollten Sie über die unmittelbaren Anforderungen der Aufgabenstellung hinausdenken und eine
möglichst flexible sowie dynamisch erweiterbare Architektur entwerfen. Achten Sie insbesondere auf eine
hohe Datenkapselung und die dynamische Nutzbarkeit der einzelnen Informationseinheiten.


Aufgaben -------------->



1 Konzeptionierung der Applikation
(3 Punkte)
Analysieren Sie die zuvor beschriebenen Anforderungen und erstellen Sie ein aussagekräftiges Use-Case-,
Package- sowie Klassendiagramm. Legen Sie die erstellten Diagramme in Ihrem GitLab-Projekt ab. Ver
wenden Sie zur Erstellung der Diagramme das LATEX-Paket tikz-uml1 innerhalb eines Overleaf-Projekts.
Laden Sie außerdem Ihren Tutor in das Projekt ein. Es genügt, die fertigen Diagramme einschließlich
der zugehörigen Quelldateien in Ihrem GitLab-Projekt hochzuladen. Die Änderungshistorie muss jedoch
weiterhin in Overleaf verfügbar sein.
(a) Erstellen Sie ein Use-Case-, ein Klassen- sowie ein Package-Diagramm, welche die in der Aufgaben
beschreibung spezifizierten Anforderungen abbilden.
(b) Konzipieren Sie geeignete Hilfsklassen, um die einzelnen in der Aufgabenbeschreibung beschriebenen
Funktionalitäten sinnvoll zu kapseln.



2 Implementierung der Aufgabenstellung
(7 Punkte)
Implementieren Sie die in Aufgabe 1 konzipierten Datenstrukturen sowie die in der Aufgabenbeschreibung
spezifizierte Applikation als Maven-Projekt. Achten Sie darauf, bei der Implementierung sowohl Interfaces
und / oder abstrakte Klassen sinnvoll einzusetzen.
(a) Implementieren Sie die in Aufgabe 1 entworfenen Klassenstrukturen und stellen Sie sicher, dass alle
Klassen über geeignete Konstruktoren verfügen.
(b) Achten Sie auf eine möglichst hohe Datenkapselung.
(c) Ermöglichen Sie die Serialisierung und Deserialisierung der implementierten Datenstrukturen (Druck
sache, Protokoll und Rede) in ein JSON-Format. Die entsprechenden JSON-Strukturen müssen über
ein geeignetes Interface im Dateisystem gespeichert und von dort wieder geladen werden können.
Erstellen Sie hierzu geeignete Testdatensätze