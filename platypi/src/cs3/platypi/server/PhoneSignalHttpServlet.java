package cs3.platypi.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cs3.platypi.shared.SignalMetadata;

@SuppressWarnings("serial")
public class PhoneSignalHttpServlet extends HttpServlet{

    private PhoneSignalServiceImpl collabService = new PhoneSignalServiceImpl();
    String[] carrierList = {"att", "verizon", "tmobile", "sprint"};

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String msg = "SignalFinderAPI=1.0\n";
        String clientId = req.getParameter("clientId");

        String carrier = req.getParameter("carrier");
        if (Arrays.asList(carrierList).indexOf(carrier) == -1) {
            msg = msg + "3\n" + "Invalid carrier.";
            resp.getWriter().write(msg);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int numData = -1;
        try {
            numData = Integer.parseInt(req.getParameter("numData"));
        } catch (NumberFormatException e) {
            msg = msg + "3\n" + "Could not parse field numData.";
            resp.getWriter().write(msg);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (numData <= 0) {
            msg = msg + "4\n" + "number of data is <= 0.";
            resp.getWriter().write(msg);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        double lat, lon, accuracy;
        String phoneType;
        long time;
        int sig;
        ArrayList<SignalMetadata> signalList = new ArrayList<SignalMetadata>();

        try {
            for (int i = 0; i < numData; i++) {
                lat = Double.parseDouble(req.getParameter("latitude" + i));
                lon = Double.parseDouble(req.getParameter("longitude" + i));
                accuracy = Double.parseDouble(req.getParameter("accuracy" + i));
                phoneType = req.getParameter("phoneType" + i);
                time = Long.parseLong(req.getParameter("time" + i));
                sig = Integer.parseInt(req.getParameter("signal" + i));
                SignalMetadata signal = new SignalMetadata(clientId, carrier, lat, lon, accuracy, phoneType, time, sig);
                signalList.add(signal);
            }
        } catch (Exception e) {
            msg = msg + "4\n" + "Could not parse signal information.\n";
            resp.getWriter().write(msg);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        collabService.saveSignalInfo(signalList);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

}
