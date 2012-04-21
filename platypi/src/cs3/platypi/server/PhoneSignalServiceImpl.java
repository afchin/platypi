package cs3.platypi.server;

import java.util.ArrayList;
import java.util.List;
import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import cs3.platypi.webapp.PhoneSignalService;
import cs3.platypi.server.PMF;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class PhoneSignalServiceImpl extends RemoteServiceServlet implements PhoneSignalService {

    @Override
    public List<SignalInfo> getSignalList() {
        PersistenceManager manager = PMF.getManager();
        try {
            Extent<SignalInfo> allSignalInfo = manager.getExtent(SignalInfo.class);
            ArrayList<SignalInfo> signalInfo = new ArrayList<SignalInfo>();

            for (SignalInfo s : allSignalInfo) {
                signalInfo.add(s);
            }
            return signalInfo;
        } finally {
            manager.close();
        }
    }

    @Override
    public void saveSignalInfo(List<SignalInfo> signalInfo) {
        PersistenceManager manager = PMF.getManager();
        try {
            manager.makePersistentAll(signalInfo);
        } finally {
            manager.close();
        }
    }

}
