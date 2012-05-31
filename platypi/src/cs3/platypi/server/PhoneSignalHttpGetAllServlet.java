package cs3.platypi.server;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cs3.platypi.shared.SignalMetadata;
 
@SuppressWarnings("serial")
public class PhoneSignalHttpGetAllServlet extends HttpServlet{
    
    private PhoneSignalServiceImpl collabService = new PhoneSignalServiceImpl();
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String msg = "SignalFinderAPI=1.0\n";
        ArrayList<SignalMetadata> signalList = (ArrayList<SignalMetadata>) collabService.getSignalList();
        JSONArray jsonSignalList = new JSONArray();
        
        try {
            for (SignalMetadata s : signalList) {
                JSONObject signalObj = new JSONObject();
                signalObj.put("clientId", s.getClientId());
                signalObj.put("carrier", s.getCarrier());
                signalObj.put("latitude", s.getLatitude());
                signalObj.put("longitude", s.getLongitude());
                signalObj.put("accuracy", s.getAccuracy());
                signalObj.put("phoneType", s.getPhoneType());
                signalObj.put("time", s.getTime());
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
