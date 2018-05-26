package fr.gallyoko.app.fastart.bdd.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import fr.gallyoko.app.fastart.bdd.FaStartBdd;
import fr.gallyoko.app.fastart.bdd.entity.ColorEntity;

public class ColorRepository {

    private static final int VERSION_BDD = 1;
    private static final String NOM_BDD = "fastart.db";

    private static final String TABLE_NAME = "color";
    private static final String COL_ID = "id";
    private static final int NUM_COL_ID = 0;
    private static final String COL_NAME = "name";
    private static final int NUM_COL_NAME = 1;
    private static final String COL_VALUE = "value";
    private static final int NUM_COL_VALUE = 2;

    private SQLiteDatabase bdd;

    private FaStartBdd faStartBdd;

    public ColorRepository(Context context){
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

    public long insert(ColorEntity color){
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associée à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(COL_NAME, color.getName());
        values.put(COL_VALUE, color.getValue());
        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(TABLE_NAME, null, values);
    }

    public int update(int id, ColorEntity color){
        ContentValues values = new ContentValues();
        values.put(COL_NAME, color.getName());
        values.put(COL_VALUE, color.getValue());
        return bdd.update(TABLE_NAME, values, COL_ID + " = " +id, null);
    }

    public int remove(int id){
        return bdd.delete(TABLE_NAME, COL_ID + " = " +id, null);
    }

    public ArrayList<ColorEntity> getAll(){
        Cursor c = bdd.query(TABLE_NAME, new String[] {COL_ID, COL_NAME, COL_VALUE}, null, null, null, null, null);
        return cursorToColorEntityArrayList(c);
    }

    public ColorEntity getByName(String name){
        Cursor c = bdd.query(TABLE_NAME, new String[] {COL_ID, COL_NAME, COL_VALUE}, COL_NAME + " LIKE \"" + name +"\"", null, null, null, null);
        return cursorToColorEntity(c);
    }

    public ColorEntity getByValue(int value){
        Cursor c = bdd.query(TABLE_NAME, new String[] {COL_ID, COL_NAME, COL_VALUE}, COL_VALUE + " = \"" + value +"\"", null, null, null, null);
        return cursorToColorEntity(c);
    }

    public ColorEntity getById(int id){
        Cursor c = bdd.query(TABLE_NAME, new String[] {COL_ID, COL_NAME, COL_VALUE}, COL_ID + " = \"" + id +"\"", null, null, null, null);
        return cursorToColorEntity(c);
    }

    //Cette méthode permet de convertir un cursor en un type
    private ColorEntity cursorToColorEntity(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        //Sinon on se place sur le premier élément
        c.moveToFirst();
        ColorEntity color = new ColorEntity();
        //on lui affecte toutes les infos grâce aux infos contenues dans le Cursor
        color.setId(c.getInt(NUM_COL_ID));
        color.setName(c.getString(NUM_COL_NAME));
        color.setValue(c.getInt(NUM_COL_VALUE));
        //On ferme le cursor
        c.close();

        return color;
    }

    private ArrayList<ColorEntity> cursorToColorEntityArrayList(Cursor c){
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;
        ArrayList<ColorEntity> colors = new ArrayList();
        //Sinon on se place sur le premier élément
        if (c.moveToFirst()) {
            do {
                ColorEntity color = new ColorEntity();
                color.setId(c.getInt(NUM_COL_ID));
                color.setName(c.getString(NUM_COL_NAME));
                color.setValue(c.getInt(NUM_COL_VALUE));
                // add the bookName into the bookTitles ArrayList
                colors.add(color);
            } while (c.moveToNext());
        }
        c.close();

        return colors;
    }
}