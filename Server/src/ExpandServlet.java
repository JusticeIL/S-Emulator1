import controller.Model;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "ExpandServlet", urlPatterns = {"/program/expand"})
public class ExpandServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int level = req.getParameter("level") != null ? Integer.parseInt(req.getParameter("level")) : 0;
        Model model = (Model) getServletContext().getAttribute("model");
        model.Expand(level);
        resp.sendRedirect(req.getContextPath() + "/program");
    }
}
