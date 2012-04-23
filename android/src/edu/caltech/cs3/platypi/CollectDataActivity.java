package edu.caltech.cs3.platypi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

    /** Expands main.xml when activity is created, and points mainText to the text field. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mainText = (TextView) findViewById(R.id.main_text);
    }

    public void resetText(View v) {
        mainText.setText(R.string.original_text);
    }

    public void getData(View v) {
        CollectDataApp app = (CollectDataApp) getApplication();
        mainText.setText(String.format("Latitude: %f%nLongitude: %f%nSignal strength: %d",
                app.getLatitude(), app.getLongitude(), app.getSignalStrength()));
        app.submitToDB(app.getLatitude(), app.getLongitude(), app.getSignalStrength());
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
}
