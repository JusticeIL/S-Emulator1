import com.google.gson.Gson;
import controller.Model;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import program.data.VariableDTO;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@WebServlet(name = "DebugServlet", urlPatterns = {"/program/debug"})
public class DebugServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        Model model = (Model) getServletContext().getAttribute("model");
        // Expects the query parameters to contain the arguments for the program
        // and the body to contain the breakpoints, one per line
        List<String> argNames = model.getProgramData().get().getProgramXArguments();
        Set<VariableDTO> args = argNames.stream().map(name -> new VariableDTO(name, Integer.parseInt(req.getParameter(name)))).collect(Collectors.toSet());
        Set<Integer> breakpoints = req.getReader().lines().map(Integer::parseInt).collect(Collectors.toSet());

        model.startDebug(args,breakpoints);
        resp.sendRedirect(req.getContextPath() + "/program");
    }
}
