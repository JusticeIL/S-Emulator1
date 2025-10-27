package servlets.program;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import configuration.CookiesAuthenticator;
import controller.MultiUserModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "ActiveProgramServlet", value = "/api/program/active")
public class ActiveProgramServlet extends HttpServlet {
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        MultiUserModel model = (MultiUserModel) getServletContext().getAttribute("model");
        CookiesAuthenticator authenticator = (CookiesAuthenticator) getServletContext().getAttribute("cookiesAuthenticator");
        authenticator.checkForUsernameThenDo(req,resp,() -> {
            //onSuccess
            String username = authenticator.getUsername(req);
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(req.getReader(), JsonObject.class);
            if (!jsonObject.has("programName")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Missing 'programName' field");
                return;
            }
            String programName = jsonObject.get("programName").getAsString();
            if (programName != null) {
                model.setActiveProgram(username, programName);
            }
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        });
    }
}