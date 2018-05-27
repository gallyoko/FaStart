package fr.gallyoko.app.fastart.bdd.entity;

public class WidgetApiEntity {

    private int id;
    private WidgetEntity widget;
    private ApiEntity api;

    public WidgetApiEntity(){}

    public WidgetApiEntity(WidgetEntity widget, ApiEntity api){
        this.widget = widget;
        this.api = api;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public WidgetEntity getWidget() {
        return this.widget;
    }

    public void setWidget(WidgetEntity widget) {
        this.widget = widget;
    }

    public ApiEntity getApi() {
        return this.api;
    }

    public void setApi(ApiEntity api) {
        this.api = api;
    }

}