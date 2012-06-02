package edu.caltech.cs3.platypi;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * UI for debugging the app. Allows user to view local database, send
 * data, and input current signal/location.
 */
public class CollectDataActivity extends Activity {
    private TextView mainText;
    private final String TAG = "CollectDataActivity";
    private SignalFinderApp app;

    /** Expands main.xml when activity is created, and points mainText to the text field. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        setContentView(R.layout.main);
        app = (SignalFinderApp) getApplication();
        mainText = (TextView) findViewById(R.id.main_text);
    }

    public void clearDb(View v) {
        mainText.setText(R.string.original_text);
        app.localSignalData.dropData();
    }

    public void insertCurrentData(View v) {
        Intent intent = new Intent(this, SingleUpdateReceiver.class);
        app.alarmManager.set(AlarmManager.RTC_WAKEUP, 0,
                PendingIntent.getBroadcast(app, 0, intent, 0));
        // setting the alarm cancels the previous setting, so have to restart
        if (app.isCollecting()) { app.turnAlarmOn(); }
    }

    public void showLocalData(View v) {
        SignalInfo signalInfo;
        int i=0;
        mainText.setText(String.format("%s, %s%n",app.carrier,app.clientId));
        Cursor cursor = app.localSignalData.cursor();
        while (cursor.moveToNext()) {
            signalInfo = new SignalInfo(cursor);
            mainText.append(Integer.toString(i++) + " " + signalInfo.toString() + "\n\n");
        }
    }
    
    public void fetchData(View v) {
        app.fetchSignalData(0, 0, 0, 0);
    }

    public void showData(View v) {
        SignalInfo signalInfo;
        int i=0;
        mainText.setText("");
        Cursor cursor = app.signalData.cursor();
        while (cursor.moveToNext()) {
            signalInfo = new SignalInfo();
            signalInfo.setLatitude(cursor.getDouble(cursor.getColumnIndex(SignalData.C_LATITTUDE)));
            signalInfo.setLongitude(cursor.getDouble(cursor.getColumnIndex(SignalData.C_LONGITUDE)));
            signalInfo.setSigStrength_dBm(cursor.getInt(cursor.getColumnIndex(SignalData.C_SIGNAL)));
            mainText.append(Integer.toString(i++) + " " + signalInfo.toString() + "\n\n");
        }
    }

    public void sendLocalData(View v) {
        app.sendLocalData();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"onDestroy");
        super.onDestroy();
    }

}
