package cs3.platypi.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import cs3.platypi.shared.SignalMetadata;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("signal")
public interface PhoneSignalService extends RemoteService {

    /**
     * Used to get a list of raw signal data.
     *
     * @return a list of the metadata of  all signal info
     */
    List<SignalMetadata> getAllSignalList();

    /**
     * Used to get a list of current signal information based on parameters.
     *
     * @param required: minLatitude, minLongitude, maxLatitude, maxLongitude
     *        optional: carrierParams, phoneTypes, clientId
     * @return a list of the metadata of the currently available signal info
     */
    List<SignalMetadata> getSignalList(Double minLatitude, Double minLongitude,
            Double maxLatitude, Double maxLongitude, List<String> carrierParams, List<String> phoneTypes,
            String clientId);

    List<SignalMetadata> getSignalList(Double minLatitude, Double minLongitude, 
            Double maxLatitude, Double maxLongitude, List<String> carrierParams, List<String> phoneTypes);

    List<SignalMetadata> getSignalList(Double minLatitude, Double minLongitude,
            Double maxLatitude, Double maxLongitude, List<String> params);

    List<SignalMetadata> getSignalList(Double minLatitude, Double minLongitude,
            Double maxLatitude, Double maxLongitude);

    /**
     * Used to save a signal.
     *
     * @param signalInfo
     *            contains signal information stored on the phone
     */
    void saveSignalInfo(List<SignalMetadata> signalInfo);

    List<SignalMetadata> getAllSignalListAvg();

    /**
     * Used to get a list of current signal information.
     *
     * @return a list of the metadata of the currently available signal info
     */

}