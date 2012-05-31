package cs3.platypi.server;

import java.util.ArrayList;
import java.util.List;
import javax.jdo.Extent;
import javax.jdo.PersistenceManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import cs3.platypi.client.PhoneSignalService;
import cs3.platypi.server.PMF;
import cs3.platypi.shared.SignalMetadata;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class PhoneSignalServiceImpl extends RemoteServiceServlet implements PhoneSignalService {

    @Override
    public List<SignalMetadata> getSignalList() {
        PersistenceManager manager = PMF.getManager();
        try {
            ArrayList<SignalMetadata> signalInfo = new ArrayList<SignalMetadata>();
            Extent<SignalInfo> allSignalInfo = manager.getExtent(SignalInfo.class);

            for (SignalInfo s : allSignalInfo) {
                signalInfo.add(s.getSignalMetadata());
            }

            return signalInfo;
        } finally {
            manager.close();
        }
    }

    @Override
    public void saveSignalInfo(List<SignalMetadata> signalInfo) {
        PersistenceManager manager = PMF.getManager();
        try {
            List<SignalInfo> signals = new ArrayList<SignalInfo>();
            for (SignalMetadata s : signalInfo) {
                signals.add(new SignalInfo(s.getClientId(), s.getCarrier(), s.getLatitude(), s.getLongitude(), s.getAccuracy(), s.getPhoneType(), s.getTime(), s.getSignal()));
            }
            manager.makePersistentAll(signals);
        } finally {
            manager.close();
        }
    }

}
