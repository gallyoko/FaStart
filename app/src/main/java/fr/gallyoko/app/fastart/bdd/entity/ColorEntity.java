package fr.gallyoko.app.fastart.bdd.entity;

public class ColorEntity {

    private int id;
    private String name;
    private int value;

    public ColorEntity(){}

    public ColorEntity(String name, int value){
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

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}