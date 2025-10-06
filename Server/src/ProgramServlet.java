import com.google.gson.Gson;
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
        Gson gson = new Gson();
        Optional<ProgramData> data = model.getProgramData();
        data.ifPresentOrElse(
                programData -> {
                    String responseJson = gson.toJson(programData);
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    try {
                        resp.getWriter().write(responseJson);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> {
                    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                }
        );
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
            resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }
    }

}
