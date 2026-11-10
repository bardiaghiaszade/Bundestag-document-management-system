package docs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ein Typ von Dokumenten.
 * Erbt die gemeinsamen Eigenschaften von Document Class
 */
public class Protokoll extends Document {

    protected final int sitzungsnummer; //Kann nicht geändert werden
    // eine finale Liste kann inhaltlich geändert werden(z.B. mit add)!
    protected final List<Rede> redeListe;


    /**
     * constructor
     * @param id id
     * @param title titel
     * @param isPublic ob man das z.B. als API Client sehen kann
     * @param sitzungsnummer die Nummer von der Sitzung
     */
    public Protokoll(int id, String title, Boolean isPublic,
                      int sitzungsnummer) {
        super(id, title, isPublic); //ruft erstmal den constructor von superclass Document auf
        this.sitzungsnummer = sitzungsnummer;
        this.redeListe = new ArrayList<>();
    }

    //Alle getter Funktionen hier sind nur innerhalb dieses packages erreichbar, weil die protected sind

    /**
     * sitzungsnummer-getter
     * @return sitzungsnummer
     */
    public int getSitzungsnummer(){
        return sitzungsnummer;
    }

    /**
     * redeListe-getter
     * @return eine Liste von Präsentationen
     */
    public List<Rede> getRedeListe(){
        return Collections.unmodifiableList(redeListe);
    }




     //Kein setter, sondern List verwaltung
     //Andere getter/setter Funktionen werden von superclass Document geerbt
     //WICHTIG: wenn wir einen getter/setter von superclass Document ändern wöllten,
     //müssten wir das wieder schreiben (mit @override), hier ist es aber nicht der Fall!


    /**
     * neue Präsentationen in die Liste bringen
     * @param rede eine neue Präsentation
     */
    public void  addRede(Rede rede){
        redeListe.add(rede);
    }

    /**
     * eine Präsentation löschen
     * @param rede die schon in der Liste ist und wird entfernt
     */
    public void  removeRede(Rede rede){
        redeListe.remove(rede);
    }


}

