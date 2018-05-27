package fr.gallyoko.app.fastart.bdd.repository;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import fr.gallyoko.app.fastart.bdd.FaStartBdd;
import fr.gallyoko.app.fastart.bdd.entity.WidgetTypeEntity;

public class WidgetTypeRepository {

    private static final int VERSION_BDD = 1;
    private static final String NOM_BDD = "fastart.db";

    private static final String TABLE_NAME = "widget_type";
    private static final String COL_ID = "id";
    private static final int NUM_COL_ID = 0;
    private static final String COL_NAME = "name";
    private static final int NUM_COL_NAME = 1;
    private static final String COL_IMG_ON = "image_on";
    private static final int NUM_COL_IMG_ON = 2;
    private static final String COL_IMG_OFF = "image_off";
    private static final int NUM_COL_IMG_OFF = 3;

    private SQLiteDatabase bdd;

    private FaStartBdd faStartBdd;

    public WidgetTypeRepository(Context context){
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

    public long insert(WidgetTypeEntity widgetType){
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associée à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(COL_NAME, widgetType.getName());
        values.put(COL_IMG_ON, widgetType.getImageOn());
        values.put(COL_IMG_OFF, widgetType.getImageOff());
        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(TABLE_NAME, null, values);
    }

    public int update(int id, WidgetTypeEntity widgetType){
        ContentValues values = new ContentValues();
        values.put(COL_NAME, widgetType.getName());
        values.put(COL_IMG_ON, widgetType.getImageOn());
        values.put(COL_IMG_OFF, widgetType.getImageOff());
        return bdd.update(TABLE_NAME, values, COL_ID + " = " +id, null);
    }

    public int remove(int id){
        return bdd.delete(TABLE_NAME, COL_ID + " = " +id, null);
    }

    public ArrayList<WidgetTypeEntity> getAll(){
        Cursor c = bdd.query(TABLE_NAME, new String[] {COL_ID, COL_NAME, COL_IMG_ON, COL_IMG_OFF}, null, null, null, null, null);
        return cursorToWidgetTypeEntityArrayList(c);
    }

    public WidgetTypeEntity getByName(String name){
        Cursor c = bdd.query(TABLE_NAME, new String[] {COL_ID, COL_NAME, COL_IMG_ON, COL_IMG_OFF}, COL_NAME + " LIKE \"" + name +"\"", null, null, null, null);
        return cursorToWidgetTypeEntity(c);
    }

    public WidgetTypeEntity getById(int id){
        Cursor c = bdd.query(TABLE_NAME, new String[] {COL_ID, COL_NAME, COL_IMG_ON, COL_IMG_OFF}, COL_ID + " = \"" + id +"\"", null, null, null, null);
        return cursorToWidgetTypeEntity(c);
    }

    //Cette méthode permet de convertir un cursor en un type
    private WidgetTypeEntity cursorToWidgetTypeEntity(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();
        WidgetTypeEntity widgetType = new WidgetTypeEntity();
        //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        widgetType.setId(c.getInt(NUM_COL_ID));
        widgetType.setName(c.getString(NUM_COL_NAME));
        widgetType.setImageOn(c.getInt(NUM_COL_IMG_ON));
        widgetType.setImageOff(c.getInt(NUM_COL_IMG_OFF));
        //On ferme le cursor
        c.close();

        return widgetType;
    }

    private ArrayList<WidgetTypeEntity> cursorToWidgetTypeEntityArrayList(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;
        ArrayList<WidgetTypeEntity> widgetTypes = new ArrayList();
        //Sinon on se place sur le premier élément
        if (c.moveToFirst()) {
            do {
                WidgetTypeEntity widgetType = new WidgetTypeEntity();
                widgetType.setId(c.getInt(NUM_COL_ID));
                widgetType.setName(c.getString(NUM_COL_NAME));
                widgetType.setImageOn(c.getInt(NUM_COL_IMG_ON));
                widgetType.setImageOff(c.getInt(NUM_COL_IMG_OFF));
                widgetTypes.add(widgetType);
            } while (c.moveToNext());
        }
        c.close();

        return widgetTypes;
    }
}