import controller.Model;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "FunctionSwitchServlet", urlPatterns = {"/program/function"})
public class FunctionSwitchServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Expects the query parameters to contain the function name to switch to
        String functionName = req.getParameter("name");
        if (functionName != null) {
            // Switch to the specified function
            Model model = (Model) getServletContext().getAttribute("model");
            model.switchFunction(functionName);
            resp.sendRedirect(req.getContextPath() + "/program");
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
