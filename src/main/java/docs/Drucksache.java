package docs;

/**
 * Ein Typ von Dokumenten.
 * Erbt die gemeinsamen Eigenschaften von Document Class
 */
public class Drucksache extends Document{


    /**
     * constructor
     * @param id id
     * @param title titel
     * @param isPublic ob man das z.B. als API Client sehen kann
     * @param text Inhalt
     */
    public Drucksache(int id, String title, Boolean isPublic,
                      String text) {
        super(id, title, isPublic,text); //ruft erstmal den constructor von superclass Document auf

    }


    //Alle getter Funktionen hier sind nur innerhalb dieses packages erreichbar, weil die protected sind

    /**
     * text-getter
     * @ text von Dokument
     */
    public String getText(){
        return text;
    }

     //Man kann für text einen setter schreiben um den Text von Drucksache aktualisieren zu können
     //Andere getter/setter Funktionen werden von superclass Document geerbt
     //WICHTIG: wenn wir einen getter/setter von superclass Document ändern wöllten,
     //müssten wir das wieder schreiben (mit @override), hier ist es aber nicht der Fall!

    /**
     * text-setter
     * @param text text(aktualisiert)
     */
    public void setText(String text) {
        this.text = text;
    }
}
