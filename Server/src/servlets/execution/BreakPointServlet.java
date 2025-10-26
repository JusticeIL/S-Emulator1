package servlets.execution;

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

@WebServlet(name = "BreakPointServlet", urlPatterns = {"/api/program/debug/breakpoint"})
public class BreakPointServlet extends HttpServlet {
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Expects the body to contain the line number of the breakpoint to remove

        CookiesAuthenticator authenticator = (CookiesAuthenticator) getServletContext().getAttribute("cookiesAuthenticator");
        authenticator.checkForUsernameThenDo(req,resp, () -> {
            //onSuccess
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(req.getReader(), JsonObject.class);
            if (!jsonObject.has("lineNumber")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Missing 'lineNumber' field");
                return;
            }

            String username = authenticator.getUsername(req);
            MultiUserModel model = (MultiUserModel) getServletContext().getAttribute("model");
            try {
                int lineNumber = jsonObject.get("lineNumber").getAsInt();
                model.removeBreakpoint(username, lineNumber);
            } catch (NumberFormatException e) {
                String lines = jsonObject.get("lineNumber").getAsString();
                if (lines.equals("all")) {
                    model.getProgramData(username).ifPresentOrElse(program -> {
                        program.getProgramInstructions().forEach(instruction -> {
                                model.removeBreakpoint(username, instruction.getId());
                        });
                        resp.setStatus(HttpServletResponse.SC_OK);
                    }, () -> {
                        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    });
                }
                else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("Invalid 'lineNumber' value");
                }
            }

        });
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Expects the body to contain the line number of the breakpoint to add
        MultiUserModel model = (MultiUserModel) getServletContext().getAttribute("model");
        CookiesAuthenticator authenticator = (CookiesAuthenticator) getServletContext().getAttribute("cookiesAuthenticator");
        authenticator.checkForUsernameThenDo(req,resp, () -> {
            //onSuccess
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(req.getReader(), JsonObject.class);
            if (!jsonObject.has("lineNumber")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Missing 'lineNumber' field");
                return;
            }

            String username = authenticator.getUsername(req);
            int lineNumber = jsonObject.get("lineNumber").getAsInt();
            model.addBreakpoint(username, lineNumber);
        });
    }
}