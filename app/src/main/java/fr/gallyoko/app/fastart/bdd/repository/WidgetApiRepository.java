package fr.gallyoko.app.fastart.bdd.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import fr.gallyoko.app.fastart.bdd.FaStartBdd;
import fr.gallyoko.app.fastart.bdd.entity.WidgetApiEntity;
import fr.gallyoko.app.fastart.bdd.entity.WidgetEntity;
import fr.gallyoko.app.fastart.bdd.entity.ApiEntity;

public class WidgetApiRepository {

    private static final int VERSION_BDD = 1;
    private static final String NOM_BDD = "fastart.db";

    private static final String TABLE_NAME = "widget_api";
    private static final String COL_ID = "id";
    private static final int NUM_COL_ID = 0;
    private static final String COL_WIDGET_ID = "widget_id";
    private static final int NUM_COL_WIDGET_ID = 1;
    private static final String COL_API_ID = "api_id";
    private static final int NUM_COL_API_ID = 2;

    private SQLiteDatabase bdd;

    private FaStartBdd faStartBdd;

    private Context context;

    public WidgetApiRepository(Context context){
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

    public long insert(WidgetApiEntity widgetApi){
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associée à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(COL_WIDGET_ID, widgetApi.getWidget().getId());
        values.put(COL_API_ID, widgetApi.getApi().getId());
        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(TABLE_NAME, null, values);
    }

    public int update(int id, WidgetApiEntity widgetApi){
        ContentValues values = new ContentValues();
        values.put(COL_WIDGET_ID, widgetApi.getWidget().getId());
        values.put(COL_API_ID, widgetApi.getApi().getId());
        return bdd.update(TABLE_NAME, values, COL_ID + " = " +id, null);
    }

    public int remove(int id){
        return bdd.delete(TABLE_NAME, COL_ID + " = " +id, null);
    }

    public ArrayList<WidgetApiEntity> getAll(){
        Cursor c = bdd.query(TABLE_NAME, new String[] {COL_ID, COL_WIDGET_ID, COL_API_ID}, null, null, null, null, null);
        return cursorToWidgetApiEntityArrayList(c);
    }

    public WidgetApiEntity getById(int id){
        Cursor c = bdd.query(TABLE_NAME, new String[] {COL_ID, COL_WIDGET_ID, COL_API_ID}, COL_ID + " = \"" + id +"\"", null, null, null, null);
        return cursorToWidgetApiEntity(c);
    }

    public ArrayList<WidgetApiEntity> getByWidget(WidgetEntity widget){
        Cursor c = bdd.query(TABLE_NAME, new String[] {COL_ID, COL_WIDGET_ID, COL_API_ID}, COL_WIDGET_ID + " = \"" + widget.getId() +"\"", null, null, null, null);
        return cursorToWidgetApiEntityArrayList(c);
    }

    public ArrayList<ApiEntity> getApiByWidget(WidgetEntity widget){
        Cursor c = bdd.query(TABLE_NAME, new String[] {COL_ID, COL_WIDGET_ID, COL_API_ID}, COL_WIDGET_ID + " = \"" + widget.getId() +"\"", null, null, null, null);
        return cursorToApiEntityArrayList(c);
    }

    //Cette méthode permet de convertir un cursor en un type
    private WidgetApiEntity cursorToWidgetApiEntity(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();
        WidgetApiEntity widgetApi = new WidgetApiEntity();
        //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        widgetApi.setId(c.getInt(NUM_COL_ID));
        WidgetRepository widgetRepository = new WidgetRepository(this.context);
        widgetRepository.open();
        WidgetEntity widget = widgetRepository.getById(c.getInt(NUM_COL_WIDGET_ID));;
        widgetApi.setWidget(widget);
        widgetRepository.close();
        ApiRepository apiRepository = new ApiRepository(this.context);
        apiRepository.open();
        ApiEntity api = apiRepository.getById(c.getInt(NUM_COL_API_ID));;
        widgetApi.setApi(api);
        apiRepository.close();
        //On ferme le cursor
        c.close();

        return widgetApi;
    }

    private ArrayList<WidgetApiEntity> cursorToWidgetApiEntityArrayList(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;
        ArrayList<WidgetApiEntity> widgetApis = new ArrayList();
        WidgetRepository widgetRepository = new WidgetRepository(this.context);
        widgetRepository.open();
        ApiRepository apiRepository = new ApiRepository(this.context);
        apiRepository.open();
        //Sinon on se place sur le premier élément
        if (c.moveToFirst()) {
            do {
                WidgetApiEntity widgetApi = new WidgetApiEntity();
                //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
                widgetApi.setId(c.getInt(NUM_COL_ID));
                WidgetEntity widget = widgetRepository.getById(c.getInt(NUM_COL_WIDGET_ID));;
                widgetApi.setWidget(widget);
                ApiEntity api = apiRepository.getById(c.getInt(NUM_COL_API_ID));;
                widgetApi.setApi(api);
                // add the bookName into the bookTitles ArrayList
                widgetApis.add(widgetApi);
            } while (c.moveToNext());
        }
        widgetRepository.close();
        apiRepository.close();

        c.close();

        return widgetApis;
    }

    private ArrayList<ApiEntity> cursorToApiEntityArrayList(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;
        ArrayList<ApiEntity> apis = new ArrayList();

        ApiRepository apiRepository = new ApiRepository(this.context);
        apiRepository.open();
        //Sinon on se place sur le premier élément
        if (c.moveToFirst()) {
            do {
                ApiEntity api = apiRepository.getById(c.getInt(NUM_COL_API_ID));
                apis.add(api);
            } while (c.moveToNext());
        }
        apiRepository.close();

        c.close();

        return apis;
    }
}