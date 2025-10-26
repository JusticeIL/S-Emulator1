package servlets.execution;

import com.google.gson.Gson;
import configuration.CookiesAuthenticator;
import controller.MultiUserModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet(name = "checkForExecutionServlet", urlPatterns = {"/api/program/checkExecution"})
public class checkForExecutionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CookiesAuthenticator authenticator = (CookiesAuthenticator) getServletContext().getAttribute("cookiesAuthenticator");
        authenticator.checkForUsernameThenDo(req, resp, ()->{
            //onSuccess
            String username = req.getParameter("username");

            if (username == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Missing username parameter.");
                return;
            }

            MultiUserModel model = (MultiUserModel) getServletContext().getAttribute("model");

            Gson gson = new Gson();
            String responseJson = gson.toJson(Map.of("status", model.isCurrentlyInExecution(username)));

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(responseJson);
        });
    }
}