package cs3.platypi.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cs3.platypi.shared.SignalMetadata;
 
@SuppressWarnings("serial")
public class PhoneSignalHttpGetServlet extends HttpServlet{
    
    private PhoneSignalServiceImpl collabService = new PhoneSignalServiceImpl();
    String[] carrierList = {"att", "verizon", "tmobile", "sprint"};
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String msg = "SignalFinderAPI=1.0\n";
        String clientId = req.getParameter("clientId");
        Double minLatitude = Double.parseDouble(req.getParameter("minLatitude"));
        Double minLongitude = Double.parseDouble(req.getParameter("minLongitude"));
        Double maxLatitude = Double.parseDouble(req.getParameter("maxLatitude"));
        Double maxLongitude = Double.parseDouble(req.getParameter("maxLongitude"));
        String phoneType = req.getParameter("phoneType");
        String carrier = req.getParameter("carrier");
        List<String> phoneTypes = new ArrayList<String>(
                Arrays.asList(phoneType));
        List<String> carriers = new ArrayList<String>(
                Arrays.asList(carrier));
        ArrayList<SignalMetadata> signalList = (ArrayList<SignalMetadata>) collabService.getSignalList(minLatitude, minLongitude, maxLatitude, maxLongitude, carriers, phoneTypes, clientId);
        JSONArray jsonSignalList = new JSONArray();
        
        try {
            for (SignalMetadata s : signalList) {
                JSONObject signalObj = new JSONObject();
                signalObj.put("latitude", s.getLatitude());
                signalObj.put("longitude", s.getLongitude());
                signalObj.put("signal", s.getSignal());
                jsonSignalList.put(signalObj);
            }
        } catch (JSONException e) {
            msg += "5\n" + "unable to query signals";
            resp.getWriter().write(msg);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
       
        resp.getWriter().write(msg + "0\n" + jsonSignalList.toString());
        resp.setStatus(HttpServletResponse.SC_OK);
    }

}
