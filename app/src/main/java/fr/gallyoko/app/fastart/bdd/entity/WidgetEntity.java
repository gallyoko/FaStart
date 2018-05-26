package fr.gallyoko.app.fastart.bdd.entity;

public class WidgetEntity {

    private int id;
    private int appWidgetId;
    private String title;
    private String textOn;
    private ColorEntity colorOn;
    private String textOff;
    private ColorEntity colorOff;
    private WidgetTypeEntity type;
    private ApiEntity api;
    private int init;

    public WidgetEntity(){}

    public WidgetEntity(int appWidgetId, String title, String textOn, ColorEntity colorOn, String textOff,
                        ColorEntity colorOff, WidgetTypeEntity type, ApiEntity api, int init){
        this.appWidgetId = appWidgetId;
        this.title = title;
        this.textOn = textOn;
        this.colorOn = colorOn;
        this.textOff = textOff;
        this.colorOff = colorOff;
        this.type = type;
        this.api = api;
        this.init = init;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAppWidgetId() {
        return this.appWidgetId;
    }

    public void setAppWidgetId(int appWidgetId) {
        this.appWidgetId = appWidgetId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTextOn() {
        return this.textOn;
    }

    public void setTextOn(String textOn) {
        this.textOn = textOn;
    }

    public ColorEntity getColorOn() {
        return this.colorOn;
    }

    public void setColorOn(ColorEntity colorOn) {
        this.colorOn = colorOn;
    }

    public String getTextOff() {
        return this.textOff;
    }

    public void setTextOff(String textOff) {
        this.textOff = textOff;
    }

    public ColorEntity getColorOff() {
        return this.colorOff;
    }

    public void setColorOff(ColorEntity colorOff) {
        this.colorOff = colorOff;
    }

    public WidgetTypeEntity getType() {
        return this.type;
    }

    public void setType(WidgetTypeEntity type) {
        this.type = type;
    }

    public ApiEntity getApi() {
        return this.api;
    }

    public void setApi(ApiEntity api) {
        this.api = api;
    }

    public int getInit() {
        return this.init;
    }

    public void setInit(int init) {
        this.init = init;
    }
}