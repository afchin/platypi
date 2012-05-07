package edu.caltech.cs3.platypi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/** Container class for signal data. */
//All fields intended to be read/writable, so they are made public
//rather than using getters and setters.
public class SignalInfo {
    public double latitude;
    public double longitude;
    public double accuracy;
    public int phoneType;
    public long time_seconds;
    public int sigStrength_dBm;

    /**
     * Returns list of name/value pairs with given integer suffix, as indicated in
     * SignalFinder API 1.0.
     */
    public List<NameValuePair> nameValuePairs(int suffix) {
        String i = Integer.toString(suffix);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("latitude" + i, Double.toString(latitude)));
        nameValuePairs.add(new BasicNameValuePair("longitude" + i, Double.toString(longitude)));
        nameValuePairs.add(new BasicNameValuePair("accuracy" + i, Double.toString(accuracy)));
        nameValuePairs.add(new BasicNameValuePair("phoneType" + i, Integer.toString(phoneType)));
        nameValuePairs.add(new BasicNameValuePair("time" + i, Long.toString(time_seconds)));
        nameValuePairs.add(new BasicNameValuePair("signal" + i, Integer.toString(sigStrength_dBm)));

        return nameValuePairs;
    }
}
