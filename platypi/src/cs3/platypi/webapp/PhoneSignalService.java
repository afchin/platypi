package cs3.platypi.webapp;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import cs3.platypi.server.SignalInfo;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("signal")
public interface PhoneSignalService extends RemoteService {

    /**
     * Used to get a list of current signal information.
     * 
     * @return a list of the metadata of the currently available signal info
     */
    List<SignalInfo> getSignalList();

    /**
     * Used to save a signal.
     * 
     * @param signalInfo
     *            contains signal information stored on the phone
     */
    void saveSignalInfo(List<SignalInfo> signalInfo);

}