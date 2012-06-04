package cs3.platypi.server;

import static org.junit.Assert.assertEquals;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletResponse;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 *  Test for the PhoneSignalHttpServelt class
 *
 */
public class PhoneSignalHttpServletTest extends TestCase {
    private final LocalServiceTestHelper helper = 
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    
    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }
    
    @Test
    public void testAddSignals() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "http://test.com/submit");
        HashMap<String, String> paramMap = new HashMap<String, String>();
        int numData = 5;
        paramMap.put("clientId", "test");
        paramMap.put("carrier", "att");
        paramMap.put("numData", "" + numData);
        for (int i = 0; i < numData; i++) {
            paramMap.put("latitude"+ i, ""+ 100.1 + i);
            paramMap.put("longitude"+ i, ""+ 200.2 + i);
            paramMap.put("accuracy" + i, "" + i);
            paramMap.put("phoneType" + i, "1");
            paramMap.put("time" +i, ""+ new Date().getTime());
            paramMap.put("signal"+ i, -113 + i + "");
        }
        request.setParameters(paramMap);
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        PhoneSignalHttpServlet servlet = new PhoneSignalHttpServlet();
        servlet.doPost(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        
        PersistenceManager manager = PMF.getManager();
        Query query = manager.newQuery(SignalInfo.class);
        query.setFilter("clientId == targetClientId");
        query.declareParameters("String targetClientId");
        assertEquals(((List<SignalInfo>) query.execute("test")).size(), numData);
    }
    
    @Test
    public void testParseError() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "http://test.com/submit");
        HashMap<String, String> paramMap = new HashMap<String, String>();
        int numData = 5;
        paramMap.put("clientId", "test");
        paramMap.put("carrier", "att");
        paramMap.put("numData", "" + numData);
        for (int i = 0; i < numData; i++) {
            paramMap.put("latitude"+ i, "test"+ 100.1 + i);
            paramMap.put("longitude"+ i, "test"+ 200.2 + i);
            paramMap.put("accuracy" + i, "test" + i);
            paramMap.put("phoneType" + i, "test");
            paramMap.put("time" +i, ""+ new Date().getTime());
            paramMap.put("signal"+ i, -113 + i + "");
        }
        request.setParameters(paramMap);
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        PhoneSignalHttpServlet servlet = new PhoneSignalHttpServlet();
        servlet.doPost(request, response);
        String[] text = response.getContentAsString().split("\n", 3);
        assertEquals("error code", 4, Integer.parseInt(text[1]));
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }
}
