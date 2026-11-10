package users.human;

/**
 * Abstrakte Basisklasse für alle Benutzer.
 * Enthält gemeinsame EigenschafteDocumentation wie ID, first/last_name
 */
public abstract class HumanUsers {

    protected final int id; //kann nicht geändert werden
    protected final String first_name; //kann nicht geändert werden
    protected final String last_name; //kann nicht geändert werden
    protected String partei; //Partei kann man ändern


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

    //Alle getter Funktionen hier sind nur innerhalb dieses packages erreichbar, weil die protected sind

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
