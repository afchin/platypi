package edu.caltech.cs3.platypi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
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
 * ((SignalFinderApp) getApplication()).get(Longitude|Latitude|SignalStrength)()
 */

public class SignalFinderApp extends Application {
    private LocationManager locationManager;
    private double latitude = 0;
    private double longitude = 0;
    private TelephonyManager telManager;
    private SignalStrengthListener signalStrengthListener;
    private int sigStrength = -1;
    private boolean updated = false;
    LocalSignalData localSignalData;

    /** Registers listeners for signal strength and location when app is first started. */
    @Override public void onCreate() {
        super.onCreate();
        localSignalData = new LocalSignalData(this);

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
                updated = true;
            }
        };
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public int getSignalStrength() { return sigStrength; }

    /** False if current data readings have already been recorded. */
    public boolean hasUpdated() { return updated; }
    
    /** To be called each time data is recorded to prevent repetition. */
    public void invalidate() { updated = false; }
    
    /** Adds current location and signal as new row of local signal data table. */
    public void insertCurrentData() {
        if (updated) {
            localSignalData.insert(latitude,longitude,sigStrength);
            invalidate();
        }
    }
    
    /**
     * Sends the contents of the local signal table of the database one row at a time.
     * If successful, the contents of the table are deleted. 
     */
    public void sendLocalData() {
        // Run in new thread to avoid locking up UI with slow internet connection.
        new Thread() {
            public void run() {
                Cursor cursor = localSignalData.cursor();
                try {
                    while (cursor.moveToNext()) {
                        double lat = cursor.getDouble(cursor
                                .getColumnIndex(LocalSignalData.C_LATITTUDE));
                        double lon = cursor.getDouble(cursor
                                .getColumnIndex(LocalSignalData.C_LONGITUDE));
                        int sig = cursor.getInt(cursor
                                .getColumnIndex(LocalSignalData.C_SIGNAL));

                        // Create a new HttpClient and Post Header
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost httppost = new HttpPost(
                                "http://proudplatypi.appspot.com/signal/http");

                        // Add data
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                        nameValuePairs.add(new BasicNameValuePair("latitude",
                                String.format("%f", lat)));
                        nameValuePairs.add(new BasicNameValuePair("longitude",
                                String.format("%f", lon)));
                        nameValuePairs.add(new BasicNameValuePair("signal",
                                String.format("%d", sig)));
                        httppost.setEntity(new UrlEncodedFormEntity(
                                nameValuePairs));

                        // Execute request
                        httpclient.execute(httppost);
                    }
                    localSignalData.dropData();
                } catch (ClientProtocolException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    appendToFile("Log.txt", "Failed to connect to internet.");
                }
            }
        }.start();
    }
    
    void appendToFile(String filename, String string) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(
                    new File(getExternalFilesDir(null), filename),
                    true); // append to the existing file
            fileOutputStream.write(string.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e2) {
            throw new RuntimeException(e2);
        } catch (IOException e2) {
            throw new RuntimeException(e2);
        }        
    }

    /** When signal strength changes, records it to sigStrength. */
    private class SignalStrengthListener extends PhoneStateListener {
        @Override public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            // get the signal strength, a value between 0 and 31, or 99.
            // 99 means "not known or not detectable"; see http://stackoverflow.com/a/3427615/104142
            sigStrength = signalStrength.getGsmSignalStrength();
            updated = true;
            super.onSignalStrengthsChanged(signalStrength);
        }
    }
    
}
