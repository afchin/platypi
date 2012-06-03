package cs3.platypi.server;

import java.text.DecimalFormat;
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
            ArrayList<SignalMetadata> signalInfoAvg = new ArrayList<SignalMetadata>();
            Extent<SignalInfoAvg> allSignalInfoAvg = manager.getExtent(SignalInfoAvg.class);

            for (SignalInfoAvg savg : allSignalInfoAvg) {
                signalInfoAvg.add(savg.getSignalMetadata());
            }

            return signalInfoAvg;
        } finally {
            manager.close();
        }
    }

    @Override
    public List<SignalMetadata> getSignalList(List<String> carrierParams) {
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
            List<SignalInfoAvg> signalsAvg = new ArrayList<SignalInfoAvg>();
            Extent<SignalInfoAvg> allSignalInfoAvg = manager.getExtent(SignalInfoAvg.class);

            for (SignalInfoAvg savg : allSignalInfoAvg) {
                signalsAvg.add(savg);
            }

            for (SignalMetadata s : signalInfo) {
                signals.add(new SignalInfo(s.getClientId(), s.getCarrier(), s.getLatitude(), s.getLongitude(), s.getAccuracy(), s.getPhoneType(), s.getTime(), s.getSignal()));

                // Do not add datapoint to consolidated datastore if accuracy is poor or faulty
                if (s.getAccuracy() < 20.0 && s.getAccuracy() > 0) {
                    // Longitude and latitude are multiples of 0.0001, roughly 10m by 10m
                    DecimalFormat fourForm = new DecimalFormat("#.####");
                    double longitude = Double.valueOf(fourForm.format(s.getLongitude()));
                    double latitude = Double.valueOf(fourForm.format(s.getLatitude()));
                    SignalInfoAvg signal = new SignalInfoAvg(longitude, latitude, s.getCarrier(), s.getPhoneType());
                    int index = signalsAvg.indexOf(signal);

                    if (index == -1) {
                        signal.setSignal(s.getSignal());
                        signalsAvg.add(signal);
                    } else {
                        signalsAvg.get(index).update(s.getSignal());
                    }
                }
            }
            manager.makePersistentAll(signals);
            manager.makePersistentAll(signalsAvg);
        } finally {
            manager.close();
        }
    }

}
