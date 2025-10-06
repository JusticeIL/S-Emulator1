import controller.Model;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "BreakPointServlet", urlPatterns = {"/program/debug/breakpoint"})
public class BreakPointServlet extends HttpServlet {
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Expects the query parameters to contain the line number of the breakpoint to remove
        int lineNumber = Integer.parseInt(req.getParameter("lineNumber"));
        Model model = (Model) getServletContext().getAttribute("model");
        model.removeBreakpoint(lineNumber);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Expects the query parameters to contain the line number of the breakpoint to add
        int lineNumber = Integer.parseInt(req.getParameter("lineNumber"));
        Model model = (Model) getServletContext().getAttribute("model");
        model.addBreakpoint(lineNumber);
    }
}
