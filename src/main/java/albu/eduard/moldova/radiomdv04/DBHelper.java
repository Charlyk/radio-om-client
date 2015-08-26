package albu.eduard.moldova.radiomdv04;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by e.albu on 15.06.2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "radiomd";
    private static final String FIRST_TABLE_NAME = "chanels";
    private static final String SECOND_TABLE_NAME = "favorites";
    private static final String ID_COLUMN = "_id";
    private static final String NAME_COLUMN = "Name";
    private static final String URL_COLUMN = "Adrress";
    private static final String BITMAP_RESOURCE = "Image";
    private Context context;

    private static final String CREATE_TABLE_1 = "CREATE TABLE " + FIRST_TABLE_NAME + " (" + ID_COLUMN +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME_COLUMN + " VARCHAR(255), " + URL_COLUMN +
            " VARCHAR(255), " + BITMAP_RESOURCE + " VARCHAR(255));";
    private static final String CREATE_TABLE_2 = "CREATE TABLE " + SECOND_TABLE_NAME + " (" + ID_COLUMN +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME_COLUMN + " VARCHAR(255), " + URL_COLUMN +
            " VARCHAR(255), " + BITMAP_RESOURCE + " VARCHAR(255));";

    private static final String DROP_TABLE_1 = "DROP TABLE IF EXISTS " + FIRST_TABLE_NAME;
    private static final String DROP_TABLE_2 = "DROP TABLE IF EXISTS " + SECOND_TABLE_NAME;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_1);
            db.execSQL(CREATE_TABLE_2);
            insertChanels(db);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(DROP_TABLE_1);
            db.execSQL(DROP_TABLE_2);
            onCreate(db);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertChanels (SQLiteDatabase db) {

        final String mChanels[][] = {{"Jurnal FM", "http://188.40.64.232:8000/", "jurnal_fm"}, {"Kiss FM", "http://89.28.66.205:8000/", "kiss_fm_little"},
                {"Muz FM", "http://live.muzfm.md:8000/muzfm", "muz_fm_moldova"}, {"Hit FM", "http://air-online2.hitfm.md/hitfm.ogg?1434794266025", "hitfm_logo"},
                {"Radio Albena", "http://109.185.191.129:49400/", "radio_albena"}, {"Fresh FM", "http://217.12.115.74:8000/", "fresh_fm"},
                {"Maestro FM", "http://live.maestrofm.md:8000/maestro", "maestro_fm"}, {"Megapolis FM", "http://89.28.22.181:8000/", "megapolisfm_mld"},
                {"Pro FM", "http://stream.profm.ro:8012/profm.mp3", "pro_fm"}, {"Publika FM", "http://live.maestrofm.md:8000/publikafm", "publika_fm"},
                {"Radio 7", "http://87.255.90.94:8000/128", "radio_7"}, {"Radio Noroc", "http://live.noroc.tv:8000/radionoroc.mp3", "noroc"},
                {"Radio Plai", "http://stream.radioplai.md:8001/RadioPlai_Online.mp3", "radio_plai"}, {"Radio Stil", "http://air-online2.radiostil.md/radiostil.mp3", "radio_stil"},
                {"Radio Zum", "http://89.33.1.83/radiozum", "radio_zum"}, {"Vocea Basarabiei", "http://arhiva.voceabasarabiei.net:8000/vb_radio", "vocea_basarabiei"},
                {"Radio Alla", "http://air-online2.radioalla.md:8008/radioalla.mp3", "radio_alla"}, {"Radio Micul Samaritean", "http://162.251.160.26:80/stream", "micul_samaritean"},
                {"Radio 21", "http://astreaming.radio21.ro:8000/radio21_aacp48k", "radio_21"}};

        ContentValues contentValues = new ContentValues();

        for (int x = 0; x < 19; x++) {
            int y = 0;
            contentValues.put(DBHelper.NAME_COLUMN, mChanels[x][y]);
            y = 1;
            contentValues.put(DBHelper.URL_COLUMN, mChanels[x][y]);
            y = 2;
            contentValues.put(DBHelper.BITMAP_RESOURCE, mChanels[x][y]);
            db.insert(DBHelper.FIRST_TABLE_NAME, null, contentValues);
        }
    }

    public ArrayList<String> getArrayList (SQLiteDatabase db){
        ArrayList<String> chanelsName = new ArrayList<String>();
        String[] column = {DBHelper.NAME_COLUMN};
        Cursor cursor = db.query(DBHelper.FIRST_TABLE_NAME, column, null, null, null, null, null);
        while (cursor.moveToNext()){
            int index = cursor.getColumnIndex(DBHelper.NAME_COLUMN);
            String name = cursor.getString(index);
            chanelsName.add(name);
        }
        return chanelsName;
    }

    public void deleteRowsFromSecondTable (String chanelName, SQLiteDatabase db) {
        String[] name = {chanelName};
        db.delete(DBHelper.SECOND_TABLE_NAME, DBHelper.NAME_COLUMN + " =? ", name);
    }

    public void insertRowsInSecondTable (String name, String adrress, SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.NAME_COLUMN, name);
        contentValues.put(DBHelper.URL_COLUMN, adrress);
        db.insert(DBHelper.SECOND_TABLE_NAME, null, contentValues);
    }

    public String chanelAdrress (String name, SQLiteDatabase db){
        String chanelsAdrress = null;
        String[] column = {DBHelper.URL_COLUMN};
        Cursor cursor = db.query(DBHelper.FIRST_TABLE_NAME, column, DBHelper.NAME_COLUMN + " = '" + name + "'", null, null, null, null);
        while (cursor.moveToNext()){
            int index = cursor.getColumnIndex(DBHelper.URL_COLUMN);
            String adrress = cursor.getString(index);
            chanelsAdrress = adrress;
        }
        return chanelsAdrress;
    }

    public String chanelIcon (String name, SQLiteDatabase db) {
        String resourceId = null;
        String[] column = {DBHelper.BITMAP_RESOURCE};
        Cursor cursor = db.query(DBHelper.FIRST_TABLE_NAME, column, DBHelper.NAME_COLUMN + " = '" + name + "'", null, null, null, null);
        while (cursor.moveToNext()){
            int index = cursor.getColumnIndex(DBHelper.BITMAP_RESOURCE);
            resourceId = cursor.getString(index);
        }
        return resourceId;
    }
}