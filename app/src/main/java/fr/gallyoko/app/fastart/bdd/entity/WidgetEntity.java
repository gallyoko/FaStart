package fr.gallyoko.app.fastart.bdd.entity;

public class WidgetEntity {

    private int id;
    private String title;
    private WidgetTypeEntity type;


    public WidgetEntity(){}

    public WidgetEntity(String title, WidgetTypeEntity type){
        this.title = title;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public WidgetTypeEntity getType() {
        return this.type;
    }

    public void setType(WidgetTypeEntity type) {
        this.type = type;
    }

    public String toString(){
        return "ID : "+id+"\nTitle : "+this.title+"\nType : "+this.type;
    }
}