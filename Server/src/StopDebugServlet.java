import controller.Model;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "StopDebugServlet", urlPatterns = {"/program/debug/stop"})
public class StopDebugServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Model model = (Model) getServletContext().getAttribute("model");
        model.stopDebug();
        resp.sendRedirect(req.getContextPath() + "/program");
    }
}
