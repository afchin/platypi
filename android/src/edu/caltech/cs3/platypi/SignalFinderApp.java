package edu.caltech.cs3.platypi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Collects location and signal strength. Location is collected from GPS, with
 * no fallback to coarse location ... yet. Longitude, latitude, and signal are
 * available to CollectDataActivity and CollectDataService via
 * ((SignalFinderApp) getApplication()).get(Longitude|Latitude|SignalStrength)()
 */

public class SignalFinderApp extends Application {
    // TODO: make these private. For now they're available for CollectDataActivity to debug.
    LocalSignalData localSignalData;
    String carrier;
    private static List<String> carriers = Arrays.asList(new String[] { "att",
            "verizon", "tmobile", "sprint" });
    String clientId;
    private static final String PREF_FILE = "PREFERENCES";
    AlarmManager alarmManager;
    boolean isCollecting;
    PendingIntent collectIntent;

    /**
     * Registers listeners for signal strength and location when app is first
     * started.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        localSignalData = new LocalSignalData(this);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(this, SingleUpdateReceiver.class);
        collectIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        // determine carrier
        TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        carrier = telManager.getNetworkOperatorName();
        if (carrier == "") { carrier = telManager.getSimOperatorName(); }
        carrier = carrier.toLowerCase();
        carrier = carrier.replaceAll("[^a-z]", "");
        if (!carriers.contains(carrier)) {
            throw new RuntimeException("Carrier " + carrier + " not supported.");
        }

        // determine (or generate) clientId
        SharedPreferences sharedPrefs = getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        clientId = sharedPrefs.getString("PREF_CLIENT_ID", null);
        if (clientId == null) {
                clientId = UUID.randomUUID().toString();
                Editor editor = sharedPrefs.edit();
                editor.putString("PREF_CLIENT_ID", clientId);
                editor.commit();
        }
    }

    public void turnAlarmOn(int delay_seconds) {
        if (isCollecting) { return; }
        Log.d("Platypi","turning alarm on");
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, -1, delay_seconds * 1000, collectIntent);
        isCollecting = true;
    }

    public void turnAlarmOff() {
        Log.d("Platypi","turning alarm off");
        alarmManager.cancel(collectIntent);
        isCollecting = false;
    }

    /**
     * Sends the contents of the local signal table of the database. If
     * successful, the contents of the table are deleted.
     */
    public void sendLocalData(final String apiroot) {
        // Run in new thread to avoid locking up UI with slow internet
        // connection.
        new Thread() {
            public void run() {
                Cursor cursor = localSignalData.cursor();
                try {
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("clientId",
                            clientId));
                    nameValuePairs.add(new BasicNameValuePair("carrier", carrier));

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
                    // Instead, should check for internet connectivity before trying
                }
            }
        } .start();
    }

    void appendToFile(String filename, String string) {
        try {
            // append to the existing file
            FileOutputStream fileOutputStream = new FileOutputStream(new File(
                    getExternalFilesDir(null), filename), true);
            fileOutputStream.write(string.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e2) {
            throw new RuntimeException(e2);
        } catch (IOException e2) {
            throw new RuntimeException(e2);
        }
    }

}
