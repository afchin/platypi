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
public class SignalData {
    static final String TAG = "SignalData";

    private static final String DB_NAME = "signaldata";
    private static final int DB_VERSION = 1;
    public static final String TABLE = "signaldata";
    public static final String C_LATITTUDE = "latitude";
    public static final String C_LONGITUDE = "longitude";
    public static final String C_SIGNAL = "signal";

    Context context;
    DbHelper dbHelper;
    SQLiteDatabase db;

    public SignalData(Context context) {
        this.context = context;
        dbHelper = new DbHelper();
    }

    public void insert(SignalInfo signalInfo) {
        db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(C_LATITTUDE, signalInfo.getLatitude());
        values.put(C_LONGITUDE, signalInfo.getLongitude());
        values.put(C_SIGNAL, signalInfo.getSigStrength_dBm());

        db.insert(TABLE, null, values);
    }

    public void insert(double lat, double lon, int signal) {
        db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(C_LATITTUDE, lat);
        values.put(C_LONGITUDE, lon);
        values.put(C_SIGNAL, signal);

        db.insert(TABLE, null, values);
    }

    public Cursor cursor() {
        db = dbHelper.getReadableDatabase();
        // select * from TABLE
        return db.query(TABLE, null, null, null, null, null, null);
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
            String sql = String.format(
                    "create table %s (%s double, %s double, %s int)",
                    TABLE, C_LATITTUDE, C_LONGITUDE, C_SIGNAL);
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
