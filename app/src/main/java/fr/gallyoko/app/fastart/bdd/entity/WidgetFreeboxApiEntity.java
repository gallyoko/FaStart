package fr.gallyoko.app.fastart.bdd.entity;

public class WidgetFreeboxApiEntity {

    private int id;
    private WidgetEntity widget;
    private FreeboxApiEntity apiFreebox;

    public WidgetFreeboxApiEntity(){}

    public WidgetFreeboxApiEntity(WidgetEntity widget, FreeboxApiEntity apiFreebox){
        this.widget = widget;
        this.apiFreebox = apiFreebox;
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

    public FreeboxApiEntity getApiFreebox() {
        return apiFreebox;
    }

    public void setApiFreebox(FreeboxApiEntity apiFreebox) {
        this.apiFreebox = apiFreebox;
    }
}