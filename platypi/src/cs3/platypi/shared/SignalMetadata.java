package cs3.platypi.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SignalMetadata implements IsSerializable {

    private String clientId;
    private String carrier;
    private double latitude;
    private double longitude;
    private double accuracy;
    private String phoneType;
    private long time;
    private int signal;
    private int numSignals;

    public SignalMetadata() {

    }

    public SignalMetadata(String clientId, String carrier, double latitude, double longitude,
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

    public SignalMetadata(double longitude, double latitude, String carrier,
             String phoneType, int signal, int numSignals) {
        super();
        this.longitude = longitude;
        this.latitude = latitude;
        this.carrier = carrier;
        this.phoneType = phoneType;
        this.signal = signal;
        this.numSignals = numSignals;
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

    public int getNumSignals() {
        return numSignals;
    }

    public void setNumSignal(int numSignals) {
        this.numSignals = numSignals;
    }

}
