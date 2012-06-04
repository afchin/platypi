package cs3.platypi.server;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

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
    public List<SignalMetadata> getAllSignalList() {
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
    public List<SignalMetadata> getAllSignalListAvg() {
        PersistenceManager manager = PMF.getManager();
        try {
            ArrayList<SignalMetadata> signalInfo = new ArrayList<SignalMetadata>();
            Extent<SignalInfoAvg> allSignalInfoAvg = manager.getExtent(SignalInfoAvg.class);

            for (SignalInfoAvg s : allSignalInfoAvg) {
                signalInfo.add(s.getSignalMetadata());
            }

            return signalInfo;

        } finally {
            manager.close();
        }
    }

    @Override
    public List<SignalMetadata> getSignalList(Double minLatitude, Double minLongitude,
            Double maxLatitude, Double maxLongitude, List<String> carrierParams, List<String> phoneTypes) {
        return getSignalList(minLatitude, minLongitude, maxLatitude, maxLongitude, carrierParams, phoneTypes, null);    
    }

    @Override
    public List<SignalMetadata> getSignalList(Double minLatitude, Double minLongitude, 
            Double maxLatitude, Double maxLongitude, List<String> params) {
        // Check if params is a list of carriers or phoneTypes
        ArrayList<String> paramList = new ArrayList<String>(
                Arrays.asList("0", "1", "2", "3"));
        if (paramList.containsAll(params)) {
            return getSignalList(minLatitude, minLongitude, maxLatitude, null, params, null);
        } else {
            return getSignalList(minLatitude, minLongitude, maxLatitude, maxLongitude, params, null, null);
        }
    }

    @Override
    public List<SignalMetadata> getSignalList(Double minLatitude, Double minLongitude,
            Double maxLatitude, Double maxLongitude) {
        return getSignalList(minLatitude, minLongitude, maxLatitude, maxLongitude, null, null, null);
    }

    @Override
    public List<SignalMetadata> getSignalList(Double minLatitude, Double minLongitude, 
            Double maxLatitude, Double maxLongitude, List<String> carrierParams, List<String> phoneTypes, String clientId) {
        PersistenceManager manager = PMF.getManager();

        // If no carrier is specified, show points from all carriers
        if (carrierParams == null) {
            String[] carrierList = {"att", "verizon", "tmobile", "sprint"};
            carrierParams = Arrays.asList(carrierList);
        }

        // If no phoneType is specified, show points from all phoneTypes
        if (phoneTypes == null) {
            String[] phoneList = {"0", "1", "2", "3"};
            phoneTypes = Arrays.asList(phoneList);
        }

        // If user requests data for a region of size larger than 1.0000 by 1.0000 (about 11km by 11km),
        // only the center 1.0000 by 1.0000 of data will be returned
        if (maxLatitude - minLatitude > 1.0) {
            Double center = (maxLatitude + minLatitude) / 2.0;
            maxLatitude = center + .5;
            minLatitude = center - .5;
        }
        if (maxLongitude - minLongitude > 1.0) {
            Double centerl = (maxLongitude + minLongitude) / 2.0;
            maxLongitude = centerl + .5;
            minLongitude = centerl - .5;
        }

        try {
            ArrayList<SignalMetadata> signalInfo = new ArrayList<SignalMetadata>();

            // If clientId is absent, returns data within the given geographic box
            // for the given phoneType and carrier from the consolidated datastore
            if (clientId == null) {
                Query q = manager.newQuery(SignalInfoAvg.class);
                q.declareImports("import java.util.List");
                Object[] parameters = {minLatitude, maxLatitude, carrierParams, phoneTypes};
                q.declareParameters("Double minLatitude, Double maxLatitude, List carrierParams, List phoneTypes");
                q.setFilter("latitude >= minLatitude && latitude <= maxLatitude && carrierParams.contains(carrier) && phoneTypes.contains(phoneType)");

                List<SignalInfoAvg> allSignalInfo = (List<SignalInfoAvg>) q.executeWithArray(parameters);
                //Extent<SignalInfo> allSignalInfo = manager.getExtent(SignalInfo.class);

                ArrayList<SignalInfoAvg> filtered = new ArrayList<SignalInfoAvg>();
                for (SignalInfoAvg savg: allSignalInfo) {
                    if (savg.getLongitude() >= minLongitude && savg.getLongitude() <= maxLongitude) {
                        filtered.add(savg);
                    }
                }

                for (SignalInfoAvg s : filtered) {
                    signalInfo.add(s.getSignalMetadata());
                }

            } else {
                // If clientId is present, returns raw data of the given clientId,
                // phoneType, and carrier within the given geographic box
                Query q = manager.newQuery(SignalInfo.class);
                q.declareImports("import java.util.List");
                Object[] parameters = {minLatitude, maxLatitude, carrierParams, phoneTypes, clientId};
                q.declareParameters("Double minLatitude, Double maxLatitude, List<String> carrierParams, List<String> phoneTypes, String myClientId");
                q.setFilter("clientId == myClientId && latitude >= minLatitude && latitude <= maxLatitude && carrier == carrierParams && phoneType == phoneTypes");
                
                List<SignalInfo> allSignalInfo = (List<SignalInfo>) q.executeWithArray(parameters);
                //Extent<SignalInfo> allSignalInfo = manager.getExtent(SignalInfo.class);

                ArrayList<SignalInfo> filtered = new ArrayList<SignalInfo>();
                for (SignalInfo s: allSignalInfo) {
                    if (s.getLongitude() >= minLongitude && s.getLongitude() <= maxLongitude) {
                        filtered.add(s);
                    }
                }

                for (SignalInfo s : filtered) {
                    signalInfo.add(s.getSignalMetadata());
                }
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
                    // Longitude and latitude are multiples of 0.001, roughly 100m by 100m
                    DecimalFormat threeForm = new DecimalFormat("#.###");
                    double longitude = Double.valueOf(threeForm.format(s.getLongitude()));
                    double latitude = Double.valueOf(threeForm.format(s.getLatitude()));
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
