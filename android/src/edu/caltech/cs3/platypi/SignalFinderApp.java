package edu.caltech.cs3.platypi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    SignalData signalData;
    String carrier;
    private static List<String> carriers = Arrays.asList(new String[] { "att",
            "verizon", "tmobile", "sprint" });
    String clientId;
    private static final String PREF_FILE = "PREFERENCES";
    AlarmManager alarmManager;
    PendingIntent collectIntent;

    /**
     * Registers listeners for signal strength and location when app is first
     * started.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        localSignalData = new LocalSignalData(this);
        signalData = new SignalData(this);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        getCarrier();
        getClientId();
        
        Intent intent = new Intent(this, SingleUpdateReceiver.class);
        collectIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
    }
    
    private void getCarrier() {
        TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        carrier = telManager.getNetworkOperatorName();
        if (carrier == "") { carrier = telManager.getSimOperatorName(); }
        carrier = carrier.toLowerCase();
        carrier = carrier.replaceAll("[^a-z]", "");
        if (!carriers.contains(carrier)) {
            throw new RuntimeException("Carrier " + carrier + " not supported.");
        }
    }

    private void getClientId() {
        SharedPreferences sharedPrefs = getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        clientId = sharedPrefs.getString("PREF_CLIENT_ID", null);
        if (clientId == null) {
                clientId = UUID.randomUUID().toString();
                Editor editor = sharedPrefs.edit();
                editor.putString("PREF_CLIENT_ID", clientId);
                editor.commit();
        }
    }
    
    private String getAPIroot() {
        SharedPreferences sharedPrefs = getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return sharedPrefs.getString("PREF_APIROOT", "http://2.proudplatypi.appspot.com");
        // TODO: if use leaves a trailing slash, fix it
    }

    private String getDisplayCarrier() {
        SharedPreferences sharedPrefs = getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return sharedPrefs.getString("PREF_DISPLAY_CARRIER", "All carriers");
        // TODO: if use leaves a trailing slash, fix it
    }
    
    private int getCollectionFreq() {
        SharedPreferences sharedPrefs = getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return sharedPrefs.getInt("PREF_COLLECTION_FREQ", 30);
    }

    public boolean isCollecting() {
        SharedPreferences sharedPrefs = getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return sharedPrefs.getBoolean("PREF_COLLECT_DATA", true);        
    }

    public void turnAlarmOn() {
        Log.d("Platypi","turning alarm on");
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, -1, getCollectionFreq() * 1000, collectIntent);
    }

    public void turnAlarmOff() {
        Log.d("Platypi","turning alarm off");
        alarmManager.cancel(collectIntent);
    }
    
    /**
     * Sends the contents of the local signal table of the database. If
     * successful, the contents of the table are deleted.
     */
    public void sendLocalData() {
        // Run in new thread to avoid locking up UI with slow internet
        // connection.
        final String apiroot = getAPIroot();
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
                        SignalInfo sigInfo = new SignalInfo(cursor);
                        nameValuePairs.addAll(sigInfo.nameValuePairs(numData));
                        numData++;
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

    public void fetchSignalData(final double minLat, final double minLon, final double maxLat,
            final double maxLon) {
        // Run in new thread to avoid locking up UI with slow internet
        // connection.
        final String apiroot = getAPIroot();
        final String carrier = getDisplayCarrier();
        new Thread() {
            public void run() {
                String url = String
                        .format("%s/1.0/data?minLatitude=%f&minLongitude=%f&maxLatitude=%f&maxLongitude=%f",
                                apiroot, minLat, minLon, maxLat, maxLon);
                if (carriers.contains(carrier)) {
                    url += "&carrier="+carrier;
                }

                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet();
                    request.setURI(new URI(url));
                    HttpResponse response = client.execute(request);
                    BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    
                    String line = in.readLine();
                    assert line=="SignalFinderAPI=1.0";
                    line = in.readLine();
                    assert line=="0";
                    String jsonString = in.readLine();
                    JSONArray jsonArray = new JSONArray(jsonString);
                    // TODO: do something smarter than dropping all the data first
                    signalData.dropData();
                    // note: a JSONArray is not iterable for some reason. 
                    for (int i=0; i<jsonArray.length(); i++) {
                        JSONObject row = jsonArray.getJSONObject(i); 
                        signalData.insert(row.getDouble("latitude"), 
                                row.getDouble("longitude"), 
                                row.getInt("signal"));
                    }
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                } catch (ClientProtocolException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } finally { }
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
