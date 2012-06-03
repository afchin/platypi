package cs3.platypi.server;

import com.google.appengine.api.datastore.Key;

import cs3.platypi.shared.SignalMetadata;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class SignalInfoAvg {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private String carrier;

    @Persistent
    private double latitude;

    @Persistent
    private double longitude;

    @Persistent
    private String phoneType;

    @Persistent
    private double signal;

    @Persistent
    private double decay;

    @Persistent
    private int numSignals;

    public SignalInfoAvg(double longitude, double latitude, String carrier,
            String phoneType) {
        super();
        this.carrier = carrier;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phoneType = phoneType;
        // decay = 1/n meaning we care about last n data points in this box
        this.decay = .001;
        this.numSignals = 1;
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

    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    public double getSignal() {
        return signal;
    }

    public void setSignal(double signal) {
        this.signal = signal;
    }

    public double getDecay() {
        return decay;
    }

    public void setDecay(double decay) {
        this.decay = decay;
    }

    public int getNumSignals() {
        return numSignals;
    }

    public void setNumSignals(int numSignals) {
        this.numSignals = numSignals;
    }

    public void update(int signal) {
        // For averaging purposes
        if (signal == 0) {
            signal = -115;
        }
        
        this.signal = ((1 - this.decay) * this.numSignals * this.signal
                      + (1 + this.numSignals * this.decay) * signal) / (this.numSignals + 1);
        this.numSignals += 1;
    }

    public SignalMetadata getSignalMetadata() {
        return new SignalMetadata(longitude, latitude, carrier, phoneType, (int) signal, numSignals);
    }

    public boolean equals(Object obj) {
        if (obj instanceof SignalInfoAvg) {
            return longitude == ((SignalInfoAvg) obj).getLongitude() &&
                   latitude == ((SignalInfoAvg) obj).getLatitude() &&
                   carrier.equals(((SignalInfoAvg) obj).getCarrier()) &&
                   phoneType.equals(((SignalInfoAvg) obj).getPhoneType());
        } else {
            return false;
        }
    }

}
