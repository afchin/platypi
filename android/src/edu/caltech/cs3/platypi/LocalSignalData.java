package edu.caltech.cs3.platypi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Interface for interacting with local database.
 */
public class LocalSignalData {
    static final String TAG = "LocalSignalData";
    
    private static final String DB_NAME = "signaldata";
    private static final int DB_VERSION = 1;
    public static final String TABLE = "localsignaldata";
    public static final String C_ID = "_id";
    public static final String C_LATITTUDE = "latitude";
    public static final String C_LONGITUDE = "longitude";
    public static final String C_SIGNAL = "signal";
    
    Context context;
    DbHelper dbHelper;
    SQLiteDatabase db;
    
    public LocalSignalData(Context context) {
        this.context = context;
        dbHelper = new DbHelper();
    }
    
    public void insert(double latitude, double longitude, int signal) {
        db = dbHelper.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        // TODO: C_ID column is left empty for now, but should probably be something
        values.put(C_LATITTUDE, latitude);
        values.put(C_LONGITUDE, longitude);
        values.put(C_SIGNAL, signal);
        
        db.insert("localsignaldata", null, values);
    }
    
    public Cursor cursor() {
        db = dbHelper.getReadableDatabase();
        return db.query("localsignaldata", null, null, null, null, null, null); // select * from TABLE
    }
    
    public void dropData() {
        db = dbHelper.getWritableDatabase();
        dbHelper.onUpgrade(db, DB_VERSION, DB_VERSION);        
    }
    
    /** Database manager. */
    class DbHelper extends SQLiteOpenHelper {

        public DbHelper() {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = String.format("create table %s (%s int primary key, %s double, %s double, %s int)",
                    TABLE, C_ID, C_LATITTUDE, C_LONGITUDE, C_SIGNAL);
            Log.d(TAG, "onCreate with SQL: " + sql);
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO maybe alter table on upgrade of schema
            // for now just delete the table and make a new one  
            db.execSQL("drop table if exists " + TABLE);
            onCreate(db);
        }
        
    }
}
