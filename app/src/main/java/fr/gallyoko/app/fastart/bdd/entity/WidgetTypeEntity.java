package fr.gallyoko.app.fastart.bdd.entity;

public class WidgetTypeEntity {

    private int id;
    private String name;

    public WidgetTypeEntity(){}

    public WidgetTypeEntity(String name){
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString(){
        return "ID : "+this.id+"\nName : "+this.name;
    }
}