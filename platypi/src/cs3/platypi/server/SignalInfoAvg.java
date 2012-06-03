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
    private double signal;

    @Persistent
	private double decay;
    
    @Persistent
    private int numSignals;

    public SignalInfoAvg(String clientId, String carrier, double latitude, double longitude,
            double accuracy, String phoneType, long time, int signal) {
        super();
        this.clientId = clientId;
        this.carrier = carrier;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.phoneType = phoneType;
        this.time = time;
        // Convert to double to maintain accuracy when computing running average
        this.signal = (double) signal;
        // decay = 1/n meaning we care about last n data points in this box
        this.decay = .001;
        this.numSignals = 1;
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
    
    public void update(long time, int signal) {
    	// Update time to time of most recently added signal
    	setTime(time);
    	
    	this.signal = ((1 - decay) * numSignals * this.signal
    			+ (1 + numSignals * decay) * signal) / (numSignals + 1);
    	this.numSignals += 1;
    }

    public SignalMetadata getSignalMetadata() {
        return new SignalMetadata(clientId, carrier, latitude, longitude, accuracy, phoneType, time, (int) signal);
    }

    
}
