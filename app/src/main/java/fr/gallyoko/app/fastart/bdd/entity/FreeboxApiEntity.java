package fr.gallyoko.app.fastart.bdd.entity;

public class FreeboxApiEntity {

    private int id;
    private String name;
    private String description;
    private String methodOn;
    private String onMsg;
    private String methodOff;
    private String offMsg;


    public FreeboxApiEntity(){}

    public FreeboxApiEntity(String name, String description, String methodOn, String onMsg,
                            String methodOff, String offMsg){
        this.name = name;
        this.description = description;
        this.methodOn = methodOn;
        this.onMsg = onMsg;
        this.methodOff = methodOff;
        this.offMsg = offMsg;
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

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMethodOn() {
        return methodOn;
    }

    public void setMethodOn(String methodOn) {
        this.methodOn = methodOn;
    }

    public String getOnMsg() {
        return onMsg;
    }

    public void setOnMsg(String onMsg) {
        this.onMsg = onMsg;
    }

    public String getMethodOff() {
        return methodOff;
    }

    public void setMethodOff(String methodOff) {
        this.methodOff = methodOff;
    }

    public String getOffMsg() {
        return offMsg;
    }

    public void setOffMsg(String offMsg) {
        this.offMsg = offMsg;
    }
}