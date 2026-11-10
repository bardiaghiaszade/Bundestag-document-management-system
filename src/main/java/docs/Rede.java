package docs;

/**
 * Ein Typ von Dokumenten.
 * Erbt die gemeinsamen Eigenschaften von Document Class
 */
public class Rede extends  Document {

    protected final String redner; //Kann nicht geändert werden
    protected final String partei; //Kann nicht geändert werden
    protected String text;


    /**
     * constructor
     * @param id id
     * @param title titel
     * @param isPublic ob man das z.B. als API Client sehen kann
     * @param redner Ein benutzer aus Abgeordneter Klasse
     * @param partei partei
     * @param text inhalt
     */
    public Rede(int id, String title, Boolean isPublic,
                      String redner, String partei, String text) {
        super(id, title, isPublic); //ruft erstmal den constructor von superclass Document auf
        this.redner = redner;
        this.partei = partei;
        this.text = text;
    }


    //Alle getter Funktionen hier sind nur innerhalb dieses packages erreichbar, weil die protected sind


    /**
     * redner-getter
     * @return Die Person die gesprochen hat
     */
    public String getRedner(){
        return redner;
    }

    /**
     * partei-getter
     * @return Die Partei von der Person die gesprochen hat
     */
    public String getPartei(){
        return partei;
    }

    /**
     * text-getter
     * @return inhalt
     */
    public String getText(){
        return text;
    }

     //Man kann für text einen setter schreiben um den Text von Drucksache aktualisieren zu können
     //Redner und Partei sind final und haben keinen setter.
     //Die andere getter/setter Funktionen werden von superclass Document geerbt
     //WICHTIG: wenn wir einen getter/setter von superclass Document ändern wöllten,
     //müssten wir das wieder schreiben (mit @override), hier ist es aber nicht der Fall!

    /**
     * text-setter
     * @param text inhalt
     */
    public void setText(String text) {
        this.text = text;
    }

}
