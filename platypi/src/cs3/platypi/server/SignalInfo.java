package cs3.platypi.server;

import com.google.appengine.api.datastore.Key;

import cs3.platypi.shared.SignalMetadata;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class SignalInfo {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private String clientId;
    
    @Persistent
    private String carrier;
    
    @Persistent
    private double latitude;

    @Persistent
    private double longitude;
    
    @Persistent
    private double accuracy;
    
    @Persistent
    private String phoneType;
    
    @Persistent
    private long time;

    @Persistent
    private int signal;

    public SignalInfo(String clientId, String carrier, double latitude, double longitude,
            double accuracy, String phoneType, long time, int signal) {
        super();
        this.clientId = clientId;
        this.carrier = carrier;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.phoneType = phoneType;
        this.time = time;
        this.signal = signal;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    public SignalMetadata getSignalMetadata() {
        return new SignalMetadata(clientId, carrier, latitude, longitude, accuracy, phoneType, time, signal);
    }

    
}
