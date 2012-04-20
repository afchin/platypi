package cs3.platypi.webapp;

import static org.junit.Assert.*;
import org.junit.Test;

public class PlatypiServletTest {

    @Test
    public void getWelcomeMessageTest() {
        String retWelcomeMsg = PlatypiServlet.getWelcomeMessage("welcome");
        String expWelcomeMsg = "welcome";
        assertTrue(retWelcomeMsg.equals(expWelcomeMsg));
    }
}
