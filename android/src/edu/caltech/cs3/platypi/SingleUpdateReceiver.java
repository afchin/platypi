package edu.caltech.cs3.platypi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
 * Fetches one datapoint (i.e. one SignalInfo) and stores it in
 * the local signal database.
 */
public class SingleUpdateReceiver extends BroadcastReceiver {
    private LocationManager locationManager;
    private TelephonyManager telManager;
    private SignalStrengthListener signalStrengthListener;
    private LocalSignalData localSignalData;
    private SignalInfo signalInfo;
    private Context context;
    private boolean gotLocation;
    private boolean gotSignal;

    // check for location and signal every this many ms
    static final int WAIT_TIME_ms = 500;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Platypi","onReceived");
        this.context = context;
        signalInfo = new SignalInfo();
        localSignalData = new LocalSignalData(context);
        listenSignal();
        listenLocation();
        // now wait for signal and location data to come in,
        // and record when it does.
        new Thread() {
            public void run() {
                while (!gotLocation || !gotSignal) {
                    // check back a bit later
                    try {
                        Thread.sleep(WAIT_TIME_ms);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                localSignalData.insert(signalInfo);
            }
        } .start();
    }

    public void listenLocation() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override public void onStatusChanged(String provider,
                    int status, Bundle extras) { }
            @Override public void onProviderEnabled(String provider) { }
            @Override public void onProviderDisabled(String provider) { }
            @Override public void onLocationChanged(Location location) {
                signalInfo.setLatitude(location.getLatitude());
                signalInfo.setLongitude(location.getLongitude());
                signalInfo.setAccuracy((int) location.getAccuracy());
                signalInfo.setTime_seconds(location.getTime() / 1000);
                locationManager.removeUpdates(this);
                gotLocation = true;
            }
        };
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        // this gets relaxed if necessary
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
    }

    public void listenSignal() {
        signalStrengthListener = new SignalStrengthListener();
        telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telManager.listen(signalStrengthListener,
                PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    /**
     * When signal strength changes, records phoneType and signal,
     * and then stops listening.
     */
    private class SignalStrengthListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            signalInfo.setPhoneType(telManager.getPhoneType());

            // signal strength
            switch (telManager.getPhoneType()) {
            case TelephonyManager.PHONE_TYPE_GSM:
                // signal strength is in "asu", a value between 0 and 31, or 99.
                // 99 means "not known or not detectable";
                // otherwise dBm = -113 + 2*asu
                // see
                // http://developer.android.com/reference/android/telephony/NeighboringCellInfo.html#getRssi%28%29
                int asu = signalStrength.getGsmSignalStrength();
                if (asu == 99) {
                    signalInfo.setSigStrength_dBm(0);
                    signalInfo.setPhoneType(TelephonyManager.PHONE_TYPE_NONE);
                    // per SignalFinder API 1.0
                } else if (asu == 0) {
                    signalInfo.setSigStrength_dBm(0);
                    // per SignalFinder API 1.0
                } else {
                    signalInfo.setSigStrength_dBm(-113 + 2 * asu);
                }
                break;
            case (TelephonyManager.PHONE_TYPE_CDMA):
                signalInfo.setSigStrength_dBm(signalStrength.getCdmaDbm());
                break;
            default: // TelephonyManager.PHONE_TYPE_NONE:
                signalInfo.setSigStrength_dBm(0);
                // per SignalFinder API 1.0
            }

            Log.d("SignalStrengthListener", String.format("%d,%d",
                    signalInfo.getPhoneType(), signalInfo.getSigStrength_dBm()));
            // stop listening
            telManager.listen(this, PhoneStateListener.LISTEN_NONE);
            gotSignal = true;
            super.onSignalStrengthsChanged(signalStrength);
        }
    }
}
