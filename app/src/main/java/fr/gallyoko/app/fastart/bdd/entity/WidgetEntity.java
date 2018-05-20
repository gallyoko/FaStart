package fr.gallyoko.app.fastart.bdd.entity;

public class WidgetEntity {

    private int id;
    private int appWidgetId;
    private String title;
    private WidgetTypeEntity type;
    private ApiEntity api;

    public WidgetEntity(){}

    public WidgetEntity(int appWidgetId, String title, WidgetTypeEntity type, ApiEntity api){
        this.appWidgetId = appWidgetId;
        this.title = title;
        this.type = type;
        this.api = api;
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
}