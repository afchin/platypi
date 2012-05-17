package edu.caltech.cs3.platypi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Collects location and signal strength. Location is collected from GPS, with
 * no fallback to coarse location ... yet. Longitude, latitude, and signal are
 * available to CollectDataActivity and CollectDataService via
 * ((SignalFinderApp) getApplication()).get(Longitude|Latitude|SignalStrength)()
 */

public class SignalFinderApp extends Application {
    private LocationManager locationManager;
    private TelephonyManager telManager;
    private SignalStrengthListener signalStrengthListener;
    LocalSignalData localSignalData;
    public SignalInfo signalInfo;
    //TODO: change apiroot to be mutable through prefs, with the following default
    private String apiroot = "http://proudplatypi.appspot.com";
    //TODO: handle uid and authToken properly
    private String clientId = "anton";

    /** Registers listeners for signal strength and location when app is first started. */
    @Override public void onCreate() {
        super.onCreate();
        signalInfo = new SignalInfo();
        localSignalData = new LocalSignalData(this);

        signalStrengthListener = new SignalStrengthListener();
        telManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        telManager.listen(signalStrengthListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        LocationListener locationListener = new LocationListener() {
            @Override public void onStatusChanged(String provider, int status, Bundle extras) { }
            @Override public void onProviderEnabled(String provider) { }
            @Override public void onProviderDisabled(String provider) { }
            @Override public void onLocationChanged(Location location) {
                signalInfo.setLatitude(location.getLatitude());
                signalInfo.setLongitude(location.getLongitude());
                signalInfo.setAccuracy((int) location.getAccuracy());
                signalInfo.setTime_seconds(new Date().getTime() / 1000);
            }
        };
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW); // this gets relaxed if necessary
        String provider = locationManager.getBestProvider(criteria, false);
        locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
    }

    /** Adds current SignalInfo as new row of local signal data table. */
    public void insertCurrentData() {
        if (signalInfo.gotAllData()) {
            localSignalData.insert(signalInfo.getLatitude(),
                    signalInfo.getLongitude(),
                    signalInfo.getAccuracy(),
                    signalInfo.getPhoneType(),
                    signalInfo.getTime_seconds(),
                    signalInfo.getSigStrength_dBm());
            signalInfo = new SignalInfo();
        }
    }

    /**
     * Sends the contents of the local signal table of the database.
     * If successful, the contents of the table are deleted.
     */
    public void sendLocalData() {
        // Run in new thread to avoid locking up UI with slow internet connection.
        new Thread() {
            public void run() {
                Cursor cursor = localSignalData.cursor();
                try {
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("clientId", clientId));
                    nameValuePairs.add(new BasicNameValuePair("carrier",
                            telManager.getNetworkOperatorName()));
                    // TODO: according to API, carrier must be one of
                    // ["att", "verizon", "tmobile", "sprint"]
                    // I believe getSimOperatorName returns slightly different strings.

                    // add everything in the local database
                    int numData = 0;
                    while (cursor.moveToNext()) {
                        SignalInfo sigInfo = new SignalInfo();
                        sigInfo.setLatitude(cursor.getDouble(cursor
                                .getColumnIndex(LocalSignalData.C_LATITTUDE)));
                        sigInfo.setLongitude(cursor.getDouble(cursor
                                .getColumnIndex(LocalSignalData.C_LONGITUDE)));
                        sigInfo.setAccuracy(cursor.getInt(cursor
                                .getColumnIndex(LocalSignalData.C_ACCURACY)));
                        sigInfo.setPhoneType(cursor.getInt(cursor
                                .getColumnIndex(LocalSignalData.C_PHONE_TYPE)));
                        sigInfo.setTime_seconds(cursor.getLong(cursor
                                .getColumnIndex(LocalSignalData.C_TIME)));
                        sigInfo.setSigStrength_dBm(cursor.getInt(cursor
                                .getColumnIndex(LocalSignalData.C_SIGNAL)));

                        numData++;
                        nameValuePairs.addAll(sigInfo.nameValuePairs(numData));
                    }
                    nameValuePairs.add(new BasicNameValuePair("numData",
                            Integer.toString(numData)));

                    // Create a new HttpClient and Post Header
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(apiroot + "/1.0/submit");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    // Execute request
                    httpclient.execute(httppost);

                    localSignalData.dropData();
                } catch (ClientProtocolException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    appendToFile("Log.txt", "Failed to connect to internet.");
                }
            }
        } .start();
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

    /** When signal strength changes, records network type, signal strength, cid, and lac. */
    private class SignalStrengthListener extends PhoneStateListener {
        @Override public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            // network type and time
            signalInfo.setPhoneType(telManager.getPhoneType());
            signalInfo.setTime_seconds((new Date()).getTime() / 1000);

            // signal strength
            switch (telManager.getPhoneType()) {
            case TelephonyManager.PHONE_TYPE_GSM:
                // signal strength is in "asu", a value between 0 and 31, or 99.
                // 99 means "not known or not detectable"; otherwise dBm = -113 + 2*asu
                // see http://developer.android.com/reference/android/telephony/NeighboringCellInfo.html#getRssi%28%29
                int asu = signalStrength.getGsmSignalStrength();
                if (asu == 99) {
                    signalInfo.setSigStrength_dBm(0);
                    signalInfo.setPhoneType(TelephonyManager.PHONE_TYPE_NONE); // per SignalFinder API 1.0
                } else if (asu==0) {
                    signalInfo.setSigStrength_dBm(0); // per SignalFinder API 1.0
                } else {
                    signalInfo.setSigStrength_dBm(-113 + 2 * asu);
                }
                break;
            case (TelephonyManager.PHONE_TYPE_CDMA):
                signalInfo.setSigStrength_dBm(signalStrength.getCdmaDbm());
                break;
            default: // TelephonyManager.PHONE_TYPE_NONE:
                signalInfo.setSigStrength_dBm(0); // per SignalFinder API 1.0
            }

            Log.d("SignalStrengthListener",String.format("%d,%d", signalInfo.getPhoneType(), signalInfo.getSigStrength_dBm()));
            super.onSignalStrengthsChanged(signalStrength);
        }
    }

}
