package edu.caltech.cs3.platypi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

/**
 * Collects location and signal strength. Location is collected from GPS, with
 * no fallback to coarse location ... yet. Longitude, latitude, and signal are
 * available to CollectDataActivity and CollectDataService via
 * ((CollectDataApp) getApplication()).get(Longitude|Latitude|SignalStrength)()
 */

public class CollectDataApp extends Application {
    private LocationManager locationManager;
    private double latitude = 0;
    private double longitude = 0;
    private TelephonyManager telManager;
    private SignalStrengthListener signalStrengthListener;
    private int sigStrength = -1;

    /** Registers listeners for signal strength and location when app is first launched. */
    @Override public void onCreate() {
        super.onCreate();

        signalStrengthListener = new SignalStrengthListener();
        telManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        telManager.listen(signalStrengthListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

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

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public int getSignalStrength() { return sigStrength; }

    /** Collects signal strength data. */
    private class SignalStrengthListener extends PhoneStateListener {
        @Override public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            // get the signal strength, a value between 0 and 31, or 99.
            // 99 means "not known or not detectable"; see http://stackoverflow.com/a/3427615/104142
            sigStrength = signalStrength.getGsmSignalStrength();
            super.onSignalStrengthsChanged(signalStrength);
        }
    }

    /**
     * Submits (latitude,longitude,signal) to a file. Will eventually submit to a 
     * database. Written with arguments (as opposed to submitCurrentValues()) because
     * we'll probably want to record values and batch submit them later. 
     */
    public void submitToDB(double lat, double lon, int sig) {
        // TODO: instead of writing to file, send to server, or write somewhere
        // and then send to server.
        final String FILENAME = "CollectDataData.txt";
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(
                    new File(getExternalFilesDir(null), FILENAME),
                    true); // append to the existing file
            fileOutputStream.write(String.format("%f,%f,%d%n",lat, lon, sig).getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
