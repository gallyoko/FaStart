package fr.gallyoko.app.fastart.bdd.entity;

public class ContentTypeEntity {

    private int id;
    private String name;
    private String value;

    public ContentTypeEntity(){}

    public ContentTypeEntity(String name, String value){
        this.name = name;
        this.value = value;
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

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}