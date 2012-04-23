package edu.caltech.cs3.platypi;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;

/** Collects and displays location and signal strength. */
public class CollectDataActivity extends Activity {
    private TextView mainText;
    private LocationManager locationManager;
    private double latitude = 0;
    private double longitude = 0;
    private TelephonyManager telManager;
    private SignalStrengthListener signalStrengthListener;
    private int sigStrength = 0;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mainText = (TextView) findViewById(R.id.main_text);

        // Register the signal strength listener to receive signal strength updates
        signalStrengthListener = new SignalStrengthListener();
        telManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        telManager.listen(signalStrengthListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        // Register the location listener with the Location Manager to receive location updates
        LocationListener locationListener = new LocationListener() {
            @Override public void onStatusChanged(String provider, int status, Bundle extras) { }
            @Override public void onProviderEnabled(String provider) { }
            @Override public void onProviderDisabled(String provider) { }
            @Override public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        };
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    public void resetText(View v) {
        mainText.setText(R.string.original_text);
    }

    public void getData(View v) {
        mainText.setText(String.format("Longitude: %f%nLatitude: %f%nSignal strength: %d",
                longitude, latitude, sigStrength));
    }

    /** Collects signal strength data. */
    private class SignalStrengthListener extends PhoneStateListener {
        @Override public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            // get the signal strength (a value between 0 and 31, or 99)
            sigStrength = signalStrength.getGsmSignalStrength();
            super.onSignalStrengthsChanged(signalStrength);
        }
    }
}
