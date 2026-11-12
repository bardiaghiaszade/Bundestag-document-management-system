package users.human;
import users.Berechtigung;

/**
 * Abstrakte Basisklasse für alle Benutzer.
 * Enthält gemeinsame EigenschaftenDocumentation wie ID, first/last_name
 */
public abstract class HumanUsers implements Berechtigung {

    // Alle private: von außen nur über getter lesbar (Datenkapselung)
    private final int id; //kann nicht geändert werden
    private final String first_name; //kann nicht geändert werden
    private final String last_name; //kann nicht geändert werden
    private String partei; //Partei kann man ändern


    //OVERLOADING --->
    //verschiedene constructors mit unterschiedlichen Argumenten

    /**
     * constructor mit partei
     * @param id id
     * @param first_name vorname
     * @param last_name nachname
     * @param partei partei
     */
    protected HumanUsers(int id, String first_name, String last_name, String partei){
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.partei = partei;
    }

    /**
     * constructor ohne partei
     * @param id id
     * @param first_name vorname
     * @param last_name nachname
     */
    protected HumanUsers(int id, String first_name, String last_name){
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    //Alle getter sind public (von außen lesbar)

    /**
     * id-getter
     * @return id
     */
    public int getId(){
        return id;
    }
    /**
     * name-getter
     * @return vorname + nachname
     */
    public String getName(){
        return first_name + " " + last_name;
    }
    /**
     * partei-getter
     * @return partei
     */
    public String getPartei(){
        return partei;
    }

    //setter kann man nur für partei definieren, weil alle andere final sind

    /**
     * partei-setter (wenn man seine Partei wechselt)
     * @param partei partei
     */
    public void setPartei(String partei) {
        this.partei = partei;
    }
}
