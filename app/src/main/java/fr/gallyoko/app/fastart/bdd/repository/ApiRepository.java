package fr.gallyoko.app.fastart.bdd.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import fr.gallyoko.app.fastart.bdd.FaStartBdd;
import fr.gallyoko.app.fastart.bdd.entity.ApiEntity;

public class ApiRepository {

    private static final int VERSION_BDD = 1;
    private static final String NOM_BDD = "fastart.db";

    private static final String TABLE_NAME = "api";
    private static final String COL_ID = "id";
    private static final int NUM_COL_ID = 0;
    private static final String COL_NAME = "name";
    private static final int NUM_COL_NAME = 1;
    private static final String COL_DESCRIPTION = "description";
    private static final int NUM_COL_DESCRIPTION = 2;
    private static final String COL_URL = "url";
    private static final int NUM_COL_URL = 3;
    private static final String COL_PUT_ON = "put_on";
    private static final int NUM_COL_PUT_ON = 4;
    private static final String COL_PUT_ON_MSG = "put_on_msg";
    private static final int NUM_COL_PUT_ON_MSG = 5;
    private static final String COL_PUT_OFF = "put_off";
    private static final int NUM_COL_PUT_OFF = 6;
    private static final String COL_PUT_OFF_MSG = "put_off_msg";
    private static final int NUM_COL_PUT_OFF_MSG = 7;

    private SQLiteDatabase bdd;

    private FaStartBdd faStartBdd;

    public ApiRepository(Context context){
        //On crée la BDD et sa table
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

    public long insert(ApiEntity api){
        ContentValues values = new ContentValues();
        values.put(COL_NAME, api.getName());
        values.put(COL_DESCRIPTION, api.getDescription());
        values.put(COL_URL, api.getUrl());
        values.put(COL_PUT_ON, api.getPutOn());
        values.put(COL_PUT_ON_MSG, api.getPutOnMsg());
        values.put(COL_PUT_OFF, api.getPutOff());
        values.put(COL_PUT_OFF_MSG, api.getPutOffMsg());
        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(TABLE_NAME, null, values);
    }

    public int update(int id, ApiEntity api){
        ContentValues values = new ContentValues();
        values.put(COL_NAME, api.getName());
        values.put(COL_DESCRIPTION, api.getDescription());
        values.put(COL_URL, api.getUrl());
        values.put(COL_PUT_ON, api.getPutOn());
        values.put(COL_PUT_ON_MSG, api.getPutOnMsg());
        values.put(COL_PUT_OFF, api.getPutOff());
        values.put(COL_PUT_OFF_MSG, api.getPutOffMsg());
        return bdd.update(TABLE_NAME, values, COL_ID + " = " +id, null);
    }

    public int remove(int id){
        return bdd.delete(TABLE_NAME, COL_ID + " = " +id, null);
    }

    public ArrayList<ApiEntity> getAll(){
        Cursor c = bdd.query(TABLE_NAME, new String[] {COL_ID, COL_NAME, COL_DESCRIPTION, COL_URL, COL_PUT_ON, COL_PUT_ON_MSG, COL_PUT_OFF, COL_PUT_OFF_MSG}, null, null, null, null, null);
        return cursorToApiEntityArrayList(c);
    }

    public ApiEntity getByName(String name){
        Cursor c = bdd.query(TABLE_NAME, new String[] {COL_ID, COL_NAME, COL_DESCRIPTION, COL_URL, COL_PUT_ON, COL_PUT_ON_MSG, COL_PUT_OFF, COL_PUT_OFF_MSG}, COL_NAME + " LIKE \"" + name +"\"", null, null, null, null);
        return cursorToApiEntity(c);
    }

    public ApiEntity getById(int id){
        Cursor c = bdd.query(TABLE_NAME, new String[] {COL_ID, COL_NAME, COL_DESCRIPTION, COL_URL, COL_PUT_ON, COL_PUT_ON_MSG, COL_PUT_OFF, COL_PUT_OFF_MSG}, COL_ID + " = \"" + id +"\"", null, null, null, null);
        return cursorToApiEntity(c);
    }

    //Cette méthode permet de convertir un cursor en un type
    private ApiEntity cursorToApiEntity(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();
        ApiEntity api = new ApiEntity();
        api.setId(c.getInt(NUM_COL_ID));
        api.setName(c.getString(NUM_COL_NAME));
        api.setDescription(c.getString(NUM_COL_DESCRIPTION));
        api.setUrl(c.getString(NUM_COL_URL));
        api.setPutOn(c.getString(NUM_COL_PUT_ON));
        api.setPutOnMsg(c.getString(NUM_COL_PUT_ON_MSG));
        api.setPutOff(c.getString(NUM_COL_PUT_OFF));
        api.setPutOffMsg(c.getString(NUM_COL_PUT_OFF_MSG));
        //On ferme le cursor
        c.close();

        return api;
    }

    private ArrayList<ApiEntity> cursorToApiEntityArrayList(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;
        ArrayList<ApiEntity> apis = new ArrayList();
        //Sinon on se place sur le premier élément
        if (c.moveToFirst()) {
            do {
                ApiEntity api = new ApiEntity();
                api.setId(c.getInt(NUM_COL_ID));
                api.setName(c.getString(NUM_COL_NAME));
                api.setDescription(c.getString(NUM_COL_DESCRIPTION));
                api.setUrl(c.getString(NUM_COL_URL));
                api.setPutOn(c.getString(NUM_COL_PUT_ON));
                api.setPutOnMsg(c.getString(NUM_COL_PUT_ON_MSG));
                api.setPutOff(c.getString(NUM_COL_PUT_OFF));
                api.setPutOffMsg(c.getString(NUM_COL_PUT_OFF_MSG));
                apis.add(api);
            } while (c.moveToNext());
        }
        c.close();

        return apis;
    }
}