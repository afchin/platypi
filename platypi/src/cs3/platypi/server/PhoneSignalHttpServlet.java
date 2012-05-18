package cs3.platypi.server;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cs3.platypi.shared.SignalMetadata;
 
@SuppressWarnings("serial")
public class PhoneSignalHttpServlet extends HttpServlet{
    
    private PhoneSignalServiceImpl collabService = new PhoneSignalServiceImpl();
    
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String latitude = req.getParameter("latitude");
        String longitude = req.getParameter("longitude");
        String Signal = req.getParameter("signal");
        
        double lat = Double.parseDouble(latitude);
        double lon = Double.parseDouble(longitude);
        int sig = Integer.parseInt(Signal);
        
        ArrayList<SignalMetadata> signalList = new ArrayList<SignalMetadata>();
        SignalMetadata signal = new SignalMetadata(lat, lon, sig);
        signalList.add(signal);
        collabService.saveSignalInfo(signalList);
        resp.setStatus(HttpServletResponse.SC_OK);
    }
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ArrayList<SignalMetadata> signalList = (ArrayList<SignalMetadata>) collabService.getSignalList();
        for (SignalMetadata s : signalList) {
            resp.getWriter().write(s.getLatitude()+","+s.getLongitude()+","+s.getSignal()+"\n");
        }
        resp.setStatus(HttpServletResponse.SC_OK);
    }

}
