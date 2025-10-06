import controller.Model;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "StepOverServlet", urlPatterns = {"/debug/step-over"})
public class StepOverServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Model model = (Model) getServletContext().getAttribute("model");
        model.stepOver();
        resp.sendRedirect(req.getContextPath() + "/program");
    }
}
