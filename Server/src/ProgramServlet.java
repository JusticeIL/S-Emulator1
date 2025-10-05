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
        Model model = (Model) getServletContext().getAttribute("model");
        Optional<ProgramData> data = model.getProgramData();
        resp.setContentType("text/html");
        data.ifPresent(programData -> {
            try {
                resp.getWriter().write("Program Name: " + programData.getProgramName() + "\n");
            } catch (IOException e) {
                try {
                    resp.getWriter().write("no program loaded yet");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Expects the body to contain the path to the program xml file

        String sprogramPath = req.getReader().readLine();
        Model model = (Model) getServletContext().getAttribute("model");
        try {
            model.loadProgram(sprogramPath);
            doGet(req, resp);
        } catch (JAXBException e) {
            resp.getWriter().write("failed to load program: " + e.getMessage());
        }
    }

}
