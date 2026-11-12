package docs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ein Typ von Dokumenten.
 * Erbt die gemeinsamen Eigenschaften von Document Class
 * Ein Protokoll besteht aus den Reden, die in der Sitzung gehalten wurden.
 */
public class Protokoll extends Document {

    private final int sitzungsnummer; //Kann nicht geändert werden
    // eine finale Liste kann inhaltlich geändert werden(z.B. mit add)!
    private final List<Rede> redeListe;

    /**
     * constructor
     * @param id id
     * @param title titel
     * @param isPublic ob man das z.B. als API Client sehen kann
     * @param sitzungsnummer die Nummer von der Sitzung
     * @param text text
     * @param createdAt Erstellungsdatum
     * @param updatedAt Datum der letzten Änderung
     * @param reden die Reden des Protokolls (neues Protokoll: leere Liste)
     */
    public Protokoll(int id, String title, boolean isPublic, int sitzungsnummer, String text,
                     LocalDateTime createdAt, LocalDateTime updatedAt, List<Rede> reden) {
        super(id, title, isPublic, text, createdAt, updatedAt); //ruft erstmal den constructor von superclass Document auf
        this.sitzungsnummer = sitzungsnummer;
        this.redeListe = new ArrayList<>(reden); //Kopie, damit die übergebene Liste nicht verändert wird
    }

    /**
     * sitzungsnummer-getter
     * @return sitzungsnummer
     */
    public int getSitzungsnummer() {
        return sitzungsnummer;
    }

    /**
     * redeListe-getter
     * @return eine Liste von Reden (nur lesbar)
     */
    public List<Rede> getRedeListe() {
        return Collections.unmodifiableList(redeListe);
    }

    //Kein setter, sondern Listenverwaltung
    //Alle anderen getter/setter Funktionen werden von superclass Document geerbt

    /**
     * neue Rede in die Liste bringen
     * es gibt kein <public> weil package-private ist und nur in package docs und
     * durch editor kann geändert werden
     * @param rede eine neue Rede
     */
    void addRede(Rede rede) {
        redeListe.add(rede);
    }

    /**
     * eine Rede löschen
     * es gibt kein <public> weil package-private ist und nur in package docs und
     * durch editor kann geändert werden
     * @param rede die schon in der Liste ist und wird entfernt
     */
    void removeRede(Rede rede) {
        redeListe.remove(rede);
    }
}