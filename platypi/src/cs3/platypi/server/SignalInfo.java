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
    private double latitude;

    @Persistent
    private double longitude;

    @Persistent
    private int signal;

    public SignalInfo(double latitude, double longitude, int signal) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
        this.signal = signal;
    }

    public SignalMetadata getSignalMetadata() {
        return new SignalMetadata(latitude, longitude, signal);
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

    public int getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

}