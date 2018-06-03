package fr.gallyoko.app.fastart.bdd.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import fr.gallyoko.app.fastart.bdd.FaStartBdd;
import fr.gallyoko.app.fastart.bdd.entity.ApiEntity;
import fr.gallyoko.app.fastart.bdd.entity.FreeboxApiEntity;
import fr.gallyoko.app.fastart.bdd.entity.WidgetApiEntity;
import fr.gallyoko.app.fastart.bdd.entity.WidgetEntity;
import fr.gallyoko.app.fastart.bdd.entity.WidgetTypeEntity;
import fr.gallyoko.app.fastart.bdd.entity.WidgetFreeboxApiEntity;

public class WidgetRepository {

    private static final int VERSION_BDD = 1;
    private static final String NOM_BDD = "fastart.db";

    private static final String TABLE_NAME = "widget";
    private static final String COL_ID = "id";
    private static final int NUM_COL_ID = 0;
    private static final String COL_APP_WIDGET_ID = "app_widget_id";
    private static final int NUM_COL_APP_WIDGET_ID = 1;
    private static final String COL_TITLE = "title";
    private static final int NUM_COL_TITLE = 2;
    private static final String COL_TYPE = "type";
    private static final int NUM_COL_TYPE = 3;
    private static final String COL_INIT = "init";
    private static final int NUM_COL_INIT = 4;

    private SQLiteDatabase bdd;
    private Context context;
    private FaStartBdd faStartBdd;

    public WidgetRepository(Context context){
        //On crée la BDD et sa table
        this.context = context;
        faStartBdd = new FaStartBdd(context, NOM_BDD, null, VERSION_BDD);
    }

    public void open(){
        //on ouvre la BDD en écriture
        bdd = faStartBdd.getWritableDatabase();
    }

    public void close(){
        //on ferme l'accès à la BDD
        bdd.close();
    }

    public SQLiteDatabase getBDD(){
        return bdd;
    }

    public long insert(WidgetEntity widget){
        ContentValues values = new ContentValues();
        values.put(COL_APP_WIDGET_ID, widget.getAppWidgetId());
        values.put(COL_TITLE, widget.getTitle());
        values.put(COL_TYPE, widget.getType().getId());
        values.put(COL_INIT, widget.getInit());
        long widgetId = bdd.insert(TABLE_NAME, null, values);
        widget.setId(((int) widgetId));
        if (widget.getApis() != null) {
            WidgetApiRepository widgetApiRepository = new WidgetApiRepository(this.context);
            widgetApiRepository.open();
            for (ApiEntity api: widget.getApis()) {
                WidgetApiEntity widgetApiEntity = new WidgetApiEntity(widget, api);
                widgetApiRepository.insert(widgetApiEntity);
            }
            widgetApiRepository.close();
        }
        if (widget.getFreeboxApis() != null) {
            WidgetFreeboxApiRepository widgetFreeboxApiRepository = new WidgetFreeboxApiRepository(this.context);
            widgetFreeboxApiRepository.open();
            for (FreeboxApiEntity apiFreebox: widget.getFreeboxApis()) {
                WidgetFreeboxApiEntity widgetFreeboxApiEntity = new WidgetFreeboxApiEntity(widget, apiFreebox);
                widgetFreeboxApiRepository.insert(widgetFreeboxApiEntity);
            }
            widgetFreeboxApiRepository.close();
        }
        return widgetId;
    }

    public int update(int id, WidgetEntity widget){
        ContentValues values = new ContentValues();
        values.put(COL_APP_WIDGET_ID, widget.getAppWidgetId());
        values.put(COL_TITLE, widget.getTitle());
        values.put(COL_TYPE, widget.getType().getId());
        values.put(COL_INIT, widget.getInit());
        return bdd.update(TABLE_NAME, values, COL_ID + " = " +id, null);
    }

    public int remove(int id){
        return bdd.delete(TABLE_NAME, COL_ID + " = " +id, null);
    }

    public WidgetEntity getById(int id){
        Cursor c = bdd.query(TABLE_NAME, new String[] {COL_ID, COL_APP_WIDGET_ID, COL_TITLE, COL_TYPE, COL_INIT}, COL_ID + " = \"" + id +"\"", null, null, null, null);
        return cursorToWidgetEntity(c);
    }

    public WidgetEntity getByAppWidgetId(int appWidgetId){
        Cursor c = bdd.query(TABLE_NAME, new String[] {COL_ID, COL_APP_WIDGET_ID, COL_TITLE, COL_TYPE, COL_INIT}, COL_APP_WIDGET_ID + " = \"" + appWidgetId +"\"", null, null, null, null);
        return cursorToWidgetEntity(c);
    }

    public ArrayList<WidgetEntity> getAll(){
        Cursor c = bdd.query(TABLE_NAME, new String[] {COL_ID, COL_APP_WIDGET_ID, COL_TITLE, COL_TYPE, COL_INIT}, null, null, null, null, null);
        return cursorToWidgetEntityArrayList(c);
    }

    //Cette méthode permet de convertir un cursor en un widget
    private WidgetEntity cursorToWidgetEntity(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();
        WidgetEntity widget = new WidgetEntity();
        //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        widget.setId(c.getInt(NUM_COL_ID));
        widget.setAppWidgetId(c.getInt(NUM_COL_APP_WIDGET_ID));
        widget.setTitle(c.getString(NUM_COL_TITLE));
        WidgetTypeRepository widgetTypeRepository = new WidgetTypeRepository(this.context);
        widgetTypeRepository.open();
        WidgetTypeEntity widgetTypeEntity = widgetTypeRepository.getById(c.getInt(NUM_COL_TYPE));
        widget.setType(widgetTypeEntity);

        WidgetApiRepository widgetApiRepository = new WidgetApiRepository(this.context);
        widgetApiRepository.open();
        ArrayList<ApiEntity> apis = widgetApiRepository.getApiByWidget(widget);
        if (apis != null) {
            widget.setApis(apis);
        }
        widgetApiRepository.close();

        WidgetFreeboxApiRepository widgetFreeboxApiRepository = new WidgetFreeboxApiRepository(this.context);
        widgetFreeboxApiRepository.open();
        ArrayList<FreeboxApiEntity> freeboxApis = widgetFreeboxApiRepository.getApiByWidget(widget);
        if (freeboxApis != null) {
            widget.setFreeboxApis(freeboxApis);
        }
        widgetFreeboxApiRepository.close();

        widget.setInit(c.getInt(NUM_COL_INIT));
        //On ferme le cursor
        c.close();
        widgetTypeRepository.close();
        return widget;
    }

    private ArrayList<WidgetEntity> cursorToWidgetEntityArrayList(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;
        ArrayList<WidgetEntity> widgets = new ArrayList();
        WidgetTypeRepository widgetTypeRepository = new WidgetTypeRepository(this.context);
        widgetTypeRepository.open();
        WidgetApiRepository widgetApiRepository = new WidgetApiRepository(this.context);
        widgetApiRepository.open();
        WidgetFreeboxApiRepository widgetFreeboxApiRepository = new WidgetFreeboxApiRepository(this.context);
        widgetFreeboxApiRepository.open();
        //Sinon on se place sur le premier élément
        if (c.moveToFirst()) {
            do {
                WidgetEntity widget = new WidgetEntity();
                widget.setId(c.getInt(NUM_COL_ID));
                widget.setAppWidgetId(c.getInt(NUM_COL_APP_WIDGET_ID));
                widget.setTitle(c.getString(NUM_COL_TITLE));
                WidgetTypeEntity widgetTypeEntity = widgetTypeRepository.getById(c.getInt(NUM_COL_TYPE));
                widget.setType(widgetTypeEntity);
                ArrayList<ApiEntity> apis = widgetApiRepository.getApiByWidget(widget);
                if (apis != null) {
                    widget.setApis(apis);
                }
                ArrayList<FreeboxApiEntity> freeboxApis = widgetFreeboxApiRepository.getApiByWidget(widget);
                if (freeboxApis != null) {
                    widget.setFreeboxApis(freeboxApis);
                }
                widget.setInit(c.getInt(NUM_COL_INIT));
                widgets.add(widget);
            } while (c.moveToNext());
        }
        c.close();
        widgetTypeRepository.close();
        widgetApiRepository.close();
        widgetFreeboxApiRepository.close();

        return widgets;
    }
}