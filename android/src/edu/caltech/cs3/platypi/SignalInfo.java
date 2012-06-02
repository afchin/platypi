package edu.caltech.cs3.platypi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/** Container class for signal data. */
public class SignalInfo {
    private double latitude;
    private double longitude;
    private int accuracy;
    private int phoneType;
    private long time_seconds;
    private int sigStrength_dBm;

    public SignalInfo() { }

    public SignalInfo(double la, double lo, int ac, int ph, long ti, int si) {
        latitude = la;
        longitude = lo;
        accuracy = ac;
        phoneType = ph;
        time_seconds = ti;
        sigStrength_dBm = si;
    }

    public SignalInfo(Cursor cursor) {
        // TODO: this only works for LocalSignalData cursor, not for SignalData cursor
        latitude = cursor.getDouble(cursor
                .getColumnIndex(LocalSignalData.C_LATITTUDE));
        longitude = cursor.getDouble(cursor
                .getColumnIndex(LocalSignalData.C_LONGITUDE));
        accuracy = cursor.getInt(cursor
                .getColumnIndex(LocalSignalData.C_ACCURACY));
        phoneType = cursor.getInt(cursor
                .getColumnIndex(LocalSignalData.C_PHONE_TYPE));
        time_seconds = cursor.getLong(cursor
                .getColumnIndex(LocalSignalData.C_TIME));
        sigStrength_dBm = cursor.getInt(cursor
                .getColumnIndex(LocalSignalData.C_SIGNAL));
    }

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

    /**
     * Produce the object which will be used to mark the datapoint on the map.
     * For now, it's a partially transparent colored circle. The radius parameter is given
     * because we will want to scale the marker based on the map zoom level.
     */
    public OverlayItem overlayItem(int radius_px) {
        int OPACITY = 64;
        GeoPoint point = new GeoPoint((int) (latitude * 1e6),
                (int) (longitude * 1e6));
        OverlayItem result = new OverlayItem(point, "", "");
                //String.format("%d Signal: %d, Accuracy: %dm",
                // phoneType, sigStrength_dBm, accuracy), Long.toString(time_seconds));

        // signal ranges from -113 to -113 + 2*31
        // rescale to range from 0 to 255
        int intensity = (sigStrength_dBm + 113) * 4;
        if (sigStrength_dBm == 0) { intensity = 0; }

        // make the color go linearly from pure blue to pure red
        // maybe tweak this to do something more clever in the future
        int red = 255 - intensity;
        int green = intensity;
        int blue = 0;

        ShapeDrawable marker = new ShapeDrawable(new OvalShape());
        // TODO: replace intensity/2 by something appropriate which puts an upper
        // bound on how opaque the marker will be. The user should be able to see
        // the underlying map
        marker.getPaint().setColor(Color.argb(OPACITY, red, green, blue));
        marker.setBounds(0, 0, radius_px, radius_px);
        result.setMarker(marker);

        return result;
    }

    @Override
    public String toString() {
        return String.format("%f,%f,%d,%d,%d", latitude, longitude, accuracy, time_seconds, sigStrength_dBm);
    }
}
