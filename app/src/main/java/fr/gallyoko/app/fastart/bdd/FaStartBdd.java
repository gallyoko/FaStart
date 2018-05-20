package fr.gallyoko.app.fastart.bdd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class FaStartBdd extends SQLiteOpenHelper {

    private static final String TABLE_WIDGET = "widget";
    private static final String WIDGET_COL_ID = "id";
    private static final String WIDGET_COL_APP_WIDGET_ID = "app_widget_id";
    private static final String WIDGET_COL_TITLE = "title";
    private static final String WIDGET_COL_TYPE = "type";
    private static final String WIDGET_COL_API = "api";

    private static final String TABLE_WIDGET_TYPE = "widget_type";
    private static final String WIDGET_TYPE_COL_ID = "id";
    private static final String WIDGET_TYPE_COL_NAME = "name";

    private static final String TABLE_API = "api";
    private static final String API_COL_ID = "id";
    private static final String API_COL_NAME = "name";
    private static final String API_COL_DESCRIPTION = "description";
    private static final String API_COL_URL = "url";
    private static final String API_COL_PUT_ON = "put_on";
    private static final String API_COL_PUT_ON_MSG = "put_on_msg";
    private static final String API_COL_PUT_OFF = "put_off";
    private static final String API_COL_PUT_OFF_MSG = "put_off_msg";

    private static final String CREATE_TABLE_WIDGET = "CREATE TABLE " + TABLE_WIDGET + " ("
            + WIDGET_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + WIDGET_COL_APP_WIDGET_ID + " INTEGER NOT NULL, "
            + WIDGET_COL_TITLE + " TEXT NOT NULL, " + WIDGET_COL_TYPE + " INTEGER NOT NULL, "
            + WIDGET_COL_API + " INTEGER NOT NULL);";

    private static final String CREATE_TABLE_WIDGET_TYPE = "CREATE TABLE " + TABLE_WIDGET_TYPE + " ("
            + WIDGET_TYPE_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + WIDGET_TYPE_COL_NAME + " TEXT NOT NULL);";

    private static final String CREATE_TABLE_API = "CREATE TABLE " + TABLE_API + " ("
            + API_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + API_COL_NAME + " TEXT NOT NULL, "
            + API_COL_DESCRIPTION + " TEXT NOT NULL, " + API_COL_URL + " TEXT NOT NULL, "
            + API_COL_PUT_ON + " TEXT NOT NULL, " + API_COL_PUT_ON_MSG + " TEXT NOT NULL, "
            + API_COL_PUT_OFF + " TEXT NOT NULL, " + API_COL_PUT_OFF_MSG + " TEXT NOT NULL);";

    public FaStartBdd(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.dropTableConfig(db);
        db.execSQL(CREATE_TABLE_WIDGET);
        this.createTableConfig(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //On peut faire ce qu'on veut ici moi j'ai décidé de supprimer la table et de la recréer
        //comme ça lorsque je change la version les id repartent de 0
        this.dropTableConfig(db);
        onCreate(db);
    }

    public void createTableConfig(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_WIDGET_TYPE);
            db.execSQL(CREATE_TABLE_API);
        } catch (Exception ex) {
            // todo
        }
    }

    private void dropTableConfig(SQLiteDatabase db) {
        try {
            db.execSQL("DROP TABLE " + TABLE_API + ";");
            db.execSQL("DROP TABLE " + TABLE_WIDGET_TYPE + ";");
        } catch (Exception ex) {
            // todo
        }
    }

}