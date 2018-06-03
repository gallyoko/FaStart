package fr.gallyoko.app.fastart.bdd.entity;

public class ConfigEntity {

    private int id;
    private String code;
    private String value;

    public ConfigEntity(){}

    public ConfigEntity(String code, String value){
        this.code = code;
        this.value = value;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}