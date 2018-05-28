package fr.gallyoko.app.fastart.bdd.entity;

import java.util.ArrayList;

public class WidgetEntity {

    private int id;
    private int appWidgetId;
    private String title;
    private WidgetTypeEntity type;
    private ArrayList<ApiEntity> apis = null;
    private int init;

    public WidgetEntity(){}

    public WidgetEntity(int appWidgetId, String title, WidgetTypeEntity type,
                        ArrayList<ApiEntity> apis, int init){
        this.appWidgetId = appWidgetId;
        this.title = title;
        this.type = type;
        this.apis = apis;
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

    public WidgetTypeEntity getType() {
        return this.type;
    }

    public void setType(WidgetTypeEntity type) {
        this.type = type;
    }

    public ArrayList<ApiEntity> getApis() {
        return this.apis;
    }

    public void setApis(ArrayList<ApiEntity> apis) {
        this.apis = apis;
    }

    public int getInit() {
        return this.init;
    }

    public void setInit(int init) {
        this.init = init;
    }
}