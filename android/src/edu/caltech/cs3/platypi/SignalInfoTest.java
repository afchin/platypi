package edu.caltech.cs3.platypi;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;

public class SignalInfoTest extends TestCase {

    @Test
    public void testexpected() {
        double latitude = 34.124356;
        double longitude = -118.123456;
        int accuracy = 23;
        int phoneType = 1;
        long time_seconds = 1337593470;
        int sigStrength_dBm = -81;

        SignalInfo signalInfo = new SignalInfo(latitude, longitude, accuracy,
                phoneType, time_seconds, sigStrength_dBm);
        
        String i = "251";
        List<NameValuePair> expected = new ArrayList<NameValuePair>();
        expected.add(new BasicNameValuePair("latitude" + i, Double.toString(latitude)));
        expected.add(new BasicNameValuePair("longitude" + i, Double.toString(longitude)));
        expected.add(new BasicNameValuePair("accuracy" + i, Integer.toString(accuracy)));
        expected.add(new BasicNameValuePair("phoneType" + i, Integer.toString(phoneType)));
        expected.add(new BasicNameValuePair("time" + i, Long.toString(time_seconds)));
        expected.add(new BasicNameValuePair("signal" + i, Integer.toString(sigStrength_dBm)));

        assertTrue(expected.equals(signalInfo.nameValuePairs(251)));
    }
}
