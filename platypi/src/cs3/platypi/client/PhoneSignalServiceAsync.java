package cs3.platypi.client;

import java.util.List;

import cs3.platypi.shared.SignalMetadata;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The client side stub for the RPC service.
 */
public interface PhoneSignalServiceAsync {

    /**
     * Used to get a list of raw signal data.
     *
     * @param callback
     *            the callback to return a list of the metadata of the currently
     *            available signal info
     */
    void getAllSignalList(AsyncCallback<List<SignalMetadata>> callback);
    
    void getAllSignalListAvg(AsyncCallback<List<SignalMetadata>> callback);

    /**
     * Used to get a list of current signal information.
     *
     * @param callback
     *            the callback to return a list of the metadata of the currently
     *            available signal info
     */
    void getSignalList(Double minLatitude, Double minLongitude, 
            Double maxLatitude, Double maxLongitude, List<String> carrierParams, List<String> phoneTypes,
            String clientId, AsyncCallback<List<SignalMetadata>> callback);

    void getSignalList(Double minLatitude, Double minLongitude, 
            Double maxLatitude, Double maxLongitude, List<String> carrierParams, List<String> phoneTypes, AsyncCallback<List<SignalMetadata>> callback);

    void getSignalList(Double minLatitude, Double minLongitude, 
            Double maxLatitude, Double maxLongitude, List<String> params, AsyncCallback<List<SignalMetadata>> callback);

    void getSignalList(Double minLatitude, Double minLongitude, 
            Double maxLatitude, Double maxLongitude, AsyncCallback<List<SignalMetadata>> callback);
    /**
     * Used to save a signal.
     *
     * @param signalInfo
     *            contains signal information stored on the phone
     */
    void saveSignalInfo(List<SignalMetadata> signalInfo, AsyncCallback<Void> callback);

}