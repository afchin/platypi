package cs3.platypi.server;

import static org.junit.Assert.assertEquals;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import cs3.platypi.shared.SignalMetadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletResponse;
import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class PhoneSignalHttpGetAllServletTest extends TestCase {
    String carrier = "att";
    private final LocalServiceTestHelper helper = 
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    
    @Before
    public void setUp() {
        helper.setUp();
        List<SignalInfoAvg> signalInfo = new ArrayList<SignalInfoAvg>();
        int numData = 5;
        
        for (int i = 0; i < numData; i++) {
            signalInfo.add(new SignalInfoAvg(100.1 + i, 200.2 + i, carrier, "1"));
        }
        PersistenceManager manager = PMF.getManager();
        manager.makePersistentAll(signalInfo);
    }
    
    @After
    public void tearDown() {
        helper.tearDown();
    }
    
    @Test
    public void testGetAllData() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "http://test.com/getAll");
        HashMap<String, String> paramMap = new HashMap<String, String>();
        request.setParameters(paramMap);
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        PhoneSignalHttpGetAllServlet servlet = new PhoneSignalHttpGetAllServlet();
        servlet.doGet(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        
        String[] text = response.getContentAsString().split("\n", 3);
        assertEquals("header", "SignalFinderAPI=1.0", text[0]);
        assertEquals("error code", 0, Integer.parseInt(text[1]));
        
        JSONArray result = null;
        try {
            result = new JSONArray(text[2]);
        } catch (JSONException e) {
            assertEquals("json array", true, false);
        }
        
        try {
            double[] lat = new double[result.length()];
            double[] longi = new double[result.length()];
            for (int i = 0; i <  result.length(); i++) {
                JSONObject signalObj = (JSONObject) result.get(i);
                lat[i] = signalObj.getDouble("latitude");
                longi[i] = signalObj.getDouble("longitude");
            }

            Arrays.sort(lat);
            Arrays.sort(longi);
            for (int i = 0; i < result.length(); i++) {
                assertEquals(200.2 + i, lat[i]);
                assertEquals(100.1 + i, longi[i]);
            }
        } catch (JSONException e) {
            assertEquals("json array", true, false);
        }
    }

}
