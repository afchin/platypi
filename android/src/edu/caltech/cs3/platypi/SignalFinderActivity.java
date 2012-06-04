package edu.caltech.cs3.platypi;

import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

/**
 * UI through which user can view the data on a map. Currently supports
 * viewing the contents of the local database, but will eventually support
 * fetching data from the server.
 */
public class SignalFinderActivity extends MapActivity {

    private SignalFinderApp app;
    private List<Overlay> mapOverlays;
    private SignalItemizedOverlay itemizedOverlay;
    private MapView mapView;

    @Override
    protected boolean isRouteDisplayed() { return false; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signalfinder);
        app = (SignalFinderApp) getApplication();

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);

        mapOverlays = mapView.getOverlays();
        loadData();
    }

    public void loadLocalData() {
        itemizedOverlay = new SignalItemizedOverlay(this);
        new Thread() {
            public void run() {
                mapOverlays.clear();
                Cursor cursor = app.localSignalData.cursor();
                while (cursor.moveToNext()) {
                    SignalInfo signalInfo = new SignalInfo(cursor);
                    itemizedOverlay.addOverlay(signalInfo.overlayItem(30));
                }
                itemizedOverlay.populateOverlay();
                mapOverlays.add(itemizedOverlay);
            }
        } .start();
    }

    public void loadData() {
        itemizedOverlay = new SignalItemizedOverlay(this);
        new Thread() {
            public void run() {
                mapOverlays.clear();
                Cursor cursor = app.signalData.cursor();
                while (cursor.moveToNext()) {
                    SignalInfo signalInfo = new SignalInfo();
                    signalInfo.setLatitude(cursor.getDouble(cursor.getColumnIndex(SignalData.C_LATITTUDE)));
                    signalInfo.setLongitude(cursor.getDouble(cursor.getColumnIndex(SignalData.C_LONGITUDE)));
                    signalInfo.setSigStrength_dBm(cursor.getInt(cursor.getColumnIndex(SignalData.C_SIGNAL)));

                    itemizedOverlay.addOverlay(signalInfo.overlayItem(30));
                }
                itemizedOverlay.populateOverlay();
                mapOverlays.add(itemizedOverlay);
            }
        } .start();
    }

    /** Expands menu.xml when the menu is called for the first time. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Starts/stops the service when menu item is clicked.
     * Also used to get to Preferences and CollectDataActivity, as well
     * as refreshing the displayed datapoints.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.prefs:
            startActivity(new Intent(this,Preferences.class));
            return true;
        case R.id.debugactivity:
            startActivity(new Intent(this,CollectDataActivity.class));
            return true;
        case R.id.load_data:
            //app.fetchSignalData(0, 0, 0, 0);
            // TODO: give above line time to finish in separate thread
            // also fetch data based on displayed bounding box
            loadData();
            return true;
        case R.id.load_local_data:
            loadLocalData();
            return true;
        default:
            return false;
        }
    }

}
