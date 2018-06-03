package fr.gallyoko.app.fastart.bdd.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import fr.gallyoko.app.fastart.bdd.FaStartBdd;
import fr.gallyoko.app.fastart.bdd.entity.FreeboxApiEntity;
import fr.gallyoko.app.fastart.bdd.entity.WidgetFreeboxApiEntity;
import fr.gallyoko.app.fastart.bdd.entity.WidgetEntity;

public class WidgetFreeboxApiRepository {

    private static final int VERSION_BDD = 1;
    private static final String NOM_BDD = "fastart.db";

    private static final String TABLE_NAME = "widget_freebox_api";
    private static final String COL_ID = "id";
    private static final int NUM_COL_ID = 0;
    private static final String COL_WIDGET_ID = "widget_id";
    private static final int NUM_COL_WIDGET_ID = 1;
    private static final String COL_API_FREEBOX_ID = "api_freebox_id";
    private static final int NUM_COL_API_FREEBOX_ID = 2;

    private SQLiteDatabase bdd;

    private FaStartBdd faStartBdd;

    private Context context;

    public WidgetFreeboxApiRepository(Context context){
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

    public long insert(WidgetFreeboxApiEntity widgetFreeboxApi){
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associée à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(COL_WIDGET_ID, widgetFreeboxApi.getWidget().getId());
        values.put(COL_API_FREEBOX_ID, widgetFreeboxApi.getApiFreebox().getId());
        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(TABLE_NAME, null, values);
    }

    public int update(int id, WidgetFreeboxApiEntity widgetFreeboxApi){
        ContentValues values = new ContentValues();
        values.put(COL_WIDGET_ID, widgetFreeboxApi.getWidget().getId());
        values.put(COL_API_FREEBOX_ID, widgetFreeboxApi.getApiFreebox().getId());
        return bdd.update(TABLE_NAME, values, COL_ID + " = " +id, null);
    }

    public int remove(int id){
        return bdd.delete(TABLE_NAME, COL_ID + " = " +id, null);
    }

    public ArrayList<WidgetFreeboxApiEntity> getAll(){
        Cursor c = bdd.query(TABLE_NAME, new String[] {COL_ID, COL_WIDGET_ID, COL_API_FREEBOX_ID}, null, null, null, null, null);
        return cursorToWidgetFreeboxApiEntityArrayList(c);
    }

    public WidgetFreeboxApiEntity getById(int id){
        Cursor c = bdd.query(TABLE_NAME, new String[] {COL_ID, COL_WIDGET_ID, COL_API_FREEBOX_ID}, COL_ID + " = \"" + id +"\"", null, null, null, null);
        return cursorToWidgetFreeboxApiEntity(c);
    }

    public ArrayList<WidgetFreeboxApiEntity> getByWidget(WidgetEntity widget){
        Cursor c = bdd.query(TABLE_NAME, new String[] {COL_ID, COL_WIDGET_ID, COL_API_FREEBOX_ID}, COL_WIDGET_ID + " = \"" + widget.getId() +"\"", null, null, null, null);
        return cursorToWidgetFreeboxApiEntityArrayList(c);
    }

    public ArrayList<FreeboxApiEntity> getApiByWidget(WidgetEntity widget){
        Cursor c = bdd.query(TABLE_NAME, new String[] {COL_ID, COL_WIDGET_ID, COL_API_FREEBOX_ID}, COL_WIDGET_ID + " = \"" + widget.getId() +"\"", null, null, null, null);
        return cursorToFreeboxApiEntityArrayList(c);
    }

    //Cette méthode permet de convertir un cursor en un type
    private WidgetFreeboxApiEntity cursorToWidgetFreeboxApiEntity(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();
        WidgetFreeboxApiEntity widgetFreeboxApi = new WidgetFreeboxApiEntity();
        widgetFreeboxApi.setId(c.getInt(NUM_COL_ID));
        WidgetRepository widgetRepository = new WidgetRepository(this.context);
        widgetRepository.open();
        WidgetEntity widget = widgetRepository.getById(c.getInt(NUM_COL_WIDGET_ID));;
        widgetFreeboxApi.setWidget(widget);
        widgetRepository.close();
        FreeboxApiRepository freeboxApiRepository = new FreeboxApiRepository(this.context);
        freeboxApiRepository.open();
        FreeboxApiEntity apiFreebox = freeboxApiRepository.getById(c.getInt(NUM_COL_API_FREEBOX_ID));
        widgetFreeboxApi.setApiFreebox(apiFreebox);
        freeboxApiRepository.close();
        //On ferme le cursor
        c.close();

        return widgetFreeboxApi;
    }

    private ArrayList<WidgetFreeboxApiEntity> cursorToWidgetFreeboxApiEntityArrayList(Cursor c){
        if (c.getCount() == 0)
            return null;
        ArrayList<WidgetFreeboxApiEntity> widgetFreeboxApis = new ArrayList();
        WidgetRepository widgetRepository = new WidgetRepository(this.context);
        widgetRepository.open();
        FreeboxApiRepository freeboxApiRepository = new FreeboxApiRepository(this.context);
        freeboxApiRepository.open();
        if (c.moveToFirst()) {
            do {
                WidgetFreeboxApiEntity widgetFreeboxApi = new WidgetFreeboxApiEntity();
                widgetFreeboxApi.setId(c.getInt(NUM_COL_ID));
                WidgetEntity widget = widgetRepository.getById(c.getInt(NUM_COL_WIDGET_ID));;
                widgetFreeboxApi.setWidget(widget);
                FreeboxApiEntity apiFreebox = freeboxApiRepository.getById(c.getInt(NUM_COL_API_FREEBOX_ID));
                widgetFreeboxApi.setApiFreebox(apiFreebox);
                widgetFreeboxApis.add(widgetFreeboxApi);
            } while (c.moveToNext());
        }
        widgetRepository.close();
        freeboxApiRepository.close();

        c.close();

        return widgetFreeboxApis;
    }

    private ArrayList<FreeboxApiEntity> cursorToFreeboxApiEntityArrayList(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;
        ArrayList<FreeboxApiEntity> freeboxApis = new ArrayList();

        FreeboxApiRepository apiFreeboxRepository = new FreeboxApiRepository(this.context);
        apiFreeboxRepository.open();
        //Sinon on se place sur le premier élément
        if (c.moveToFirst()) {
            do {
                FreeboxApiEntity apiFreebox = apiFreeboxRepository.getById(c.getInt(NUM_COL_API_FREEBOX_ID));
                freeboxApis.add(apiFreebox);
            } while (c.moveToNext());
        }
        apiFreeboxRepository.close();

        c.close();

        return freeboxApis;
    }
}