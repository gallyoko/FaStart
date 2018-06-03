package fr.gallyoko.app.fastart.bdd.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import fr.gallyoko.app.fastart.bdd.FaStartBdd;
import fr.gallyoko.app.fastart.bdd.entity.FreeboxApiEntity;

public class FreeboxApiRepository {

    private static final int VERSION_BDD = 1;
    private static final String NOM_BDD = "fastart.db";

    private static final String TABLE_NAME = "freebox_api";
    private static final String COL_ID = "id";
    private static final int NUM_COL_ID = 0;
    private static final String COL_NAME = "name";
    private static final int NUM_COL_NAME = 1;
    private static final String COL_DESCRIPTION = "description";
    private static final int NUM_COL_DESCRIPTION = 2;
    private static final String COL_METHOD_ON = "method_on";
    private static final int NUM_COL_METHOD_ON = 3;
    private static final String COL_ON_MSG = "on_msg";
    private static final int NUM_COL_ON_MSG = 4;
    private static final String COL_METHOD_OFF = "method_off";
    private static final int NUM_COL_METHOD_OFF = 5;
    private static final String COL_OFF_MSG = "off_msg";
    private static final int NUM_COL_OFF_MSG = 6;

    private SQLiteDatabase bdd;

    private FaStartBdd faStartBdd;

    public FreeboxApiRepository(Context context){
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

    public long insert(FreeboxApiEntity apiFreebox){
        ContentValues values = new ContentValues();
        values.put(COL_NAME, apiFreebox.getName());
        values.put(COL_DESCRIPTION, apiFreebox.getDescription());
        values.put(COL_METHOD_ON, apiFreebox.getMethodOn());
        values.put(COL_ON_MSG, apiFreebox.getOnMsg());
        values.put(COL_METHOD_OFF, apiFreebox.getMethodOff());
        values.put(COL_OFF_MSG, apiFreebox.getOffMsg());
        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(TABLE_NAME, null, values);
    }

    public int update(int id, FreeboxApiEntity apiFreebox){
        ContentValues values = new ContentValues();
        values.put(COL_NAME, apiFreebox.getName());
        values.put(COL_DESCRIPTION, apiFreebox.getDescription());
        values.put(COL_METHOD_ON, apiFreebox.getMethodOn());
        values.put(COL_ON_MSG, apiFreebox.getOnMsg());
        values.put(COL_METHOD_OFF, apiFreebox.getMethodOff());
        values.put(COL_OFF_MSG, apiFreebox.getOffMsg());
        return bdd.update(TABLE_NAME, values, COL_ID + " = " +id, null);
    }

    public int remove(int id){
        return bdd.delete(TABLE_NAME, COL_ID + " = " +id, null);
    }

    public ArrayList<FreeboxApiEntity> getAll(){
        Cursor c = bdd.query(TABLE_NAME, new String[] {COL_ID, COL_NAME, COL_DESCRIPTION, COL_METHOD_ON, COL_ON_MSG, COL_METHOD_OFF, COL_OFF_MSG}, null, null, null, null, null);
        return cursorToFreeboxApiEntityArrayList(c);
    }

    public FreeboxApiEntity getByName(String name){
        Cursor c = bdd.query(TABLE_NAME, new String[] {COL_ID, COL_NAME, COL_DESCRIPTION, COL_METHOD_ON, COL_ON_MSG, COL_METHOD_OFF, COL_OFF_MSG}, COL_NAME + " LIKE \"" + name +"\"", null, null, null, null);
        return cursorToFreeboxApiEntity(c);
    }

    public FreeboxApiEntity getById(int id){
        Cursor c = bdd.query(TABLE_NAME, new String[] {COL_ID, COL_NAME, COL_DESCRIPTION, COL_METHOD_ON, COL_ON_MSG, COL_METHOD_OFF, COL_OFF_MSG}, COL_ID + " = \"" + id +"\"", null, null, null, null);
        return cursorToFreeboxApiEntity(c);
    }

    //Cette méthode permet de convertir un cursor en un type
    private FreeboxApiEntity cursorToFreeboxApiEntity(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();
        FreeboxApiEntity freeboxApi = new FreeboxApiEntity();
        freeboxApi.setId(c.getInt(NUM_COL_ID));
        freeboxApi.setName(c.getString(NUM_COL_NAME));
        freeboxApi.setDescription(c.getString(NUM_COL_DESCRIPTION));
        freeboxApi.setMethodOn(c.getString(NUM_COL_METHOD_ON));
        freeboxApi.setOnMsg(c.getString(NUM_COL_ON_MSG));
        freeboxApi.setMethodOff(c.getString(NUM_COL_METHOD_OFF));
        freeboxApi.setOffMsg(c.getString(NUM_COL_OFF_MSG));
        //On ferme le cursor
        c.close();

        return freeboxApi;
    }

    private ArrayList<FreeboxApiEntity> cursorToFreeboxApiEntityArrayList(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;
        ArrayList<FreeboxApiEntity> freeboxApis = new ArrayList();
        //Sinon on se place sur le premier élément
        if (c.moveToFirst()) {
            do {
                FreeboxApiEntity freeboxApi = new FreeboxApiEntity();
                freeboxApi.setId(c.getInt(NUM_COL_ID));
                freeboxApi.setName(c.getString(NUM_COL_NAME));
                freeboxApi.setDescription(c.getString(NUM_COL_DESCRIPTION));
                freeboxApi.setMethodOn(c.getString(NUM_COL_METHOD_ON));
                freeboxApi.setOnMsg(c.getString(NUM_COL_ON_MSG));
                freeboxApi.setMethodOff(c.getString(NUM_COL_METHOD_OFF));
                freeboxApi.setOffMsg(c.getString(NUM_COL_OFF_MSG));
                freeboxApis.add(freeboxApi);
            } while (c.moveToNext());
        }
        c.close();

        return freeboxApis;
    }
}