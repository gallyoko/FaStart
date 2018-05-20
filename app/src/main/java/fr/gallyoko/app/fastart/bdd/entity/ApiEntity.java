package fr.gallyoko.app.fastart.bdd.entity;

public class ApiEntity {

    private int id;
    private String name;
    private String description;
    private String url;
    private String putOn;
    private String putOnMsg;
    private String putOff;
    private String putOffMsg;


    public ApiEntity(){}

    public ApiEntity(String name, String description, String url, String putOn,
                     String putOnMsg, String putOff, String putOffMsg){
        this.name = name;
        this.description = description;
        this.url = url;
        this.putOn = putOn;
        this.putOnMsg = putOnMsg;
        this.putOff = putOff;
        this.putOffMsg = putOffMsg;
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

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPutOn() { return this.putOn; }

    public void setPutOn(String putOn) {
        this.putOn = putOn;
    }

    public String getPutOnMsg() { return this.putOnMsg; }

    public void setPutOnMsg(String putOnMsg) {
        this.putOnMsg = putOnMsg;
    }

    public String getPutOff() { return this.putOff; }

    public void setPutOff(String putOff) {
        this.putOff = putOff;
    }

    public String getPutOffMsg() {
        return this.putOffMsg;
    }

    public void setPutOffMsg(String putOffMsg) {
        this.putOffMsg = putOffMsg;
    }



}