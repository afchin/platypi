package edu.caltech.cs3.platypi;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * UI through which the user can start/stop the data collection service.
 * User can also push a button to display current location/signal.
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

    public void showLocalData(View v) {
        mainText.setText("");
        Cursor cursor = app.localSignalData.cursor();
        mainText.append(String.format("%s\n%s\n%s", Build.MODEL, Build.PRODUCT, Build.FINGERPRINT));
        while (cursor.moveToNext()) {
            mainText.append(String.format("%f,%f,%d,%d,%d,%d,%d%n",
                    cursor.getDouble(cursor.getColumnIndex(LocalSignalData.C_LATITTUDE)),
                    cursor.getDouble(cursor.getColumnIndex(LocalSignalData.C_LONGITUDE)),
                    cursor.getDouble(cursor.getColumnIndex(LocalSignalData.C_ACCURACY)),
                    cursor.getInt(cursor.getColumnIndex(LocalSignalData.C_PHONE_TYPE)),
                    cursor.getLong(cursor.getColumnIndex(LocalSignalData.C_TIME)),
                    cursor.getInt(cursor.getColumnIndex(LocalSignalData.C_SIGNAL))
                    ));
        }
    }

    public void sendLocalData(View v) {
        app.sendLocalData();
    }

    /** Expands menu.xml when the menu is called for the first time. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /** Starts/stops the service when menu item is clicked. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG,"onOptionsItemSelected");
        Intent serviceIntent = new Intent(this,CollectDataService.class);
        switch(item.getItemId()) {
        case R.id.start_service:
            startService(serviceIntent);
            return true;
        case R.id.stop_service:
            stopService(serviceIntent);
            return true;
        default:
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"onDestroy");
        super.onDestroy();
    }

}
