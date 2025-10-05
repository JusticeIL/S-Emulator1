import controller.Model;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.bind.JAXBException;
import program.data.ProgramData;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "ProgramServlet", urlPatterns = {"/program"})
public class ProgramServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Model model = (Model) getServletContext().getContext("model");
        Optional<ProgramData> data = model.getProgramData();
        resp.setContentType("text/html");
        if (data.isPresent()) {
            resp.getWriter().write("<html><body><h2>Get Program Succeeded.</h2></body></html>");
        } else {
            resp.getWriter().write("<html><body><h2>No program loaded.</h2></body></html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sprogramPath = req.getParameter("path");
        Model model = (Model) getServletContext().getContext("model");
        try {
            model.loadProgram(sprogramPath);
            doGet(req, resp);
        } catch (JAXBException e) {
            resp.getWriter().write("failed to load program: " + e.getMessage());
        }
    }
}
