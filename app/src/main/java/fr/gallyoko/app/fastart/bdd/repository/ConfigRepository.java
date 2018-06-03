package fr.gallyoko.app.fastart.bdd.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import fr.gallyoko.app.fastart.bdd.FaStartBdd;
import fr.gallyoko.app.fastart.bdd.entity.ConfigEntity;

public class ConfigRepository {

    private static final int VERSION_BDD = 1;
    private static final String NOM_BDD = "fastart.db";

    private static final String TABLE_NAME = "config";
    private static final String COL_ID = "id";
    private static final int NUM_COL_ID = 0;
    private static final String COL_CODE = "code";
    private static final int NUM_COL_CODE = 1;
    private static final String COL_VALUE = "value";
    private static final int NUM_COL_VALUE = 2;

    private SQLiteDatabase bdd;

    private FaStartBdd faStartBdd;

    public ConfigRepository(Context context){
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

    public long insert(ConfigEntity config){
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associée à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(COL_CODE, config.getCode());
        values.put(COL_VALUE, config.getValue());
        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(TABLE_NAME, null, values);
    }

    public int update(int id, ConfigEntity config){
        ContentValues values = new ContentValues();
        values.put(COL_CODE, config.getCode());
        values.put(COL_VALUE, config.getValue());
        return bdd.update(TABLE_NAME, values, COL_ID + " = " +id, null);
    }

    public int remove(int id){
        return bdd.delete(TABLE_NAME, COL_ID + " = " +id, null);
    }

    public ArrayList<ConfigEntity> getAll(){
        Cursor c = bdd.query(TABLE_NAME, new String[] {COL_ID, COL_CODE, COL_VALUE}, null, null, null, null, null);
        return cursorToConfigEntityArrayList(c);
    }

    public ConfigEntity getByCode(String code){
        Cursor c = bdd.query(TABLE_NAME, new String[] {COL_ID, COL_CODE, COL_VALUE}, COL_CODE + " = \"" + code +"\"", null, null, null, null);
        return cursorToConfigEntity(c);
    }

    public ConfigEntity getById(int id){
        Cursor c = bdd.query(TABLE_NAME, new String[] {COL_ID, COL_CODE, COL_VALUE}, COL_ID + " = \"" + id +"\"", null, null, null, null);
        return cursorToConfigEntity(c);
    }

    //Cette méthode permet de convertir un cursor en un type
    private ConfigEntity cursorToConfigEntity(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();
        ConfigEntity config = new ConfigEntity();
        //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        config.setId(c.getInt(NUM_COL_ID));
        config.setCode(c.getString(NUM_COL_CODE));
        config.setValue(c.getString(NUM_COL_VALUE));
        //On ferme le cursor
        c.close();

        return config;
    }

    private ArrayList<ConfigEntity> cursorToConfigEntityArrayList(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;
        ArrayList<ConfigEntity> configs = new ArrayList();
        //Sinon on se place sur le premier élément
        if (c.moveToFirst()) {
            do {
                ConfigEntity config = new ConfigEntity();
                config.setId(c.getInt(NUM_COL_ID));
                config.setCode(c.getString(NUM_COL_CODE));
                config.setValue(c.getString(NUM_COL_VALUE));
                // add the bookName into the bookTitles ArrayList
                configs.add(config);
            } while (c.moveToNext());
        }
        c.close();

        return configs;
    }
}