package edu.caltech.cs3.platypi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/** Container class for signal data. */
public class SignalInfo {
    private double latitude;
    private double longitude;
    private int accuracy;
    private int phoneType;
    private long time_seconds;
    private int sigStrength_dBm;

    public void setLatitude(double latitude) {
        this.latitude = latitude; }
    public void setLongitude(double longitude) {
        this.longitude = longitude; }
    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy; }
    public void setPhoneType(int phoneType) {
        this.phoneType = phoneType; }
    public void setTime_seconds(long time_seconds) {
        this.time_seconds = time_seconds; }
    public void setSigStrength_dBm(int sigStrength_dBm) {
        this.sigStrength_dBm = sigStrength_dBm; }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public double getAccuracy() { return accuracy; }
    public int getPhoneType() { return phoneType; }
    public long getTime_seconds() { return time_seconds; }
    public int getSigStrength_dBm() { return sigStrength_dBm; }
    
    /**
     * Returns list of name/value pairs with given integer suffix, as indicated in
     * SignalFinder API 1.0.
     */
    public List<NameValuePair> nameValuePairs(int suffix) {
        String i = Integer.toString(suffix);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("latitude" + i, Double.toString(latitude)));
        nameValuePairs.add(new BasicNameValuePair("longitude" + i, Double.toString(longitude)));
        nameValuePairs.add(new BasicNameValuePair("accuracy" + i, Integer.toString(accuracy)));
        nameValuePairs.add(new BasicNameValuePair("phoneType" + i, Integer.toString(phoneType)));
        nameValuePairs.add(new BasicNameValuePair("time" + i, Long.toString(time_seconds)));
        nameValuePairs.add(new BasicNameValuePair("signal" + i, Integer.toString(sigStrength_dBm)));

        return nameValuePairs;
    }
}
