package servlets.execution;

import com.google.gson.Gson;
import configuration.CookiesAuthenticator;
import controller.MultiUserModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dto.VariableDTO;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@WebServlet(name = "ProgramExecutionServlet", urlPatterns = {"/api/program/execute"})
public class ProgramExecutionServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();

        // Expects the query parameters to contain the arguments for the program
        CookiesAuthenticator authenticator = (CookiesAuthenticator) getServletContext().getAttribute("cookiesAuthenticator");
        authenticator.checkForUsernameThenDo(req,resp,() -> {
            //onSuccess
            String username = authenticator.getUsername(req);
            MultiUserModel model = (MultiUserModel) getServletContext().getAttribute("model");
            List<String> argNames = model.getProgramData(username).get().getProgramXArguments();
            String architectureGeneration = req.getReader().readLine();
            Set<VariableDTO> args = argNames.stream().map(name -> new VariableDTO(name, Integer.parseInt(req.getParameter(name)))).collect(Collectors.toSet());
            model.runProgram(username, args, architectureGeneration);
            resp.sendRedirect(req.getContextPath() + "/program");
        });
    }
}
