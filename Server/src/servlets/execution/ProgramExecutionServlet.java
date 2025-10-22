package servlets.execution;

import com.google.gson.Gson;
import configuration.CookiesAuthenticator;
import controller.MultiUserModel;
import dto.ExecutionPayload;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dto.VariableDTO;

import java.io.BufferedReader;
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
            ExecutionPayload executionPayload = parsePayload(req);
            String username = authenticator.getUsername(req);
            MultiUserModel model = (MultiUserModel) getServletContext().getAttribute("model");
            final Set<VariableDTO> args = executionPayload.getArguments();
            final String architectureGeneration = executionPayload.getArchitecture();
            model.runProgram(username, args, architectureGeneration);
            resp.sendRedirect(req.getContextPath() + "/api/program");
        });
    }

    private ExecutionPayload parsePayload(HttpServletRequest req) throws IOException {
        Gson gson = new Gson();
        StringBuilder jsonBody = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBody.append(line);
            }
        }

        // 2. ניתוח ה-JSON לאובייקט ExecutionPayload
        return gson.fromJson(jsonBody.toString(), ExecutionPayload.class);
    }
}
