package cs3.platypi.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SignalMetadata implements IsSerializable {

    private double latitude;
    private double longitude;
    private int signal;

    public SignalMetadata() {

    }

    public SignalMetadata(double latitude, double longitude, int signal) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
        this.signal = signal;
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

    public String toString(){
      return ((Double)latitude).toString() + " " + ((Double)longitude).toString() +
          " " + ((Integer)signal).toString();
    }
}
