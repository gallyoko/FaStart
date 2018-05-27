package fr.gallyoko.app.fastart.bdd.entity;

public class WidgetTypeEntity {

    private int id;
    private String name;
    private int imageOn;
    private int imageOff;

    public WidgetTypeEntity(){}

    public WidgetTypeEntity(String name, int imageOn, int imageOff){
        this.name = name;
        this.imageOn = imageOn;
        this.imageOff = imageOff;
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

    public int getImageOn() {
        return this.imageOn;
    }

    public void setImageOn(int imageOn) {
        this.imageOn = imageOn;
    }

    public int getImageOff() {
        return this.imageOff;
    }

    public void setImageOff(int imageOff) {
        this.imageOff = imageOff;
    }
}