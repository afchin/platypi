package cs3.platypi.server;

import java.io.IOException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class PlatypiServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().println("Hello, world");
    }

    protected static String getWelcomeMessage(String welcome) {
        return welcome;
    }
}
