package servlets.program;

import configuration.CookiesAuthenticator;
import controller.MultiUserModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "FunctionSwitchServlet", urlPatterns = {"/api/program/function"})
public class FunctionSwitchServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Expects the query parameters to contain the function name to switch to
        String functionName = req.getParameter("name");
        if (functionName != null) {
            // Switch to the specified function
            MultiUserModel model = (MultiUserModel) getServletContext().getAttribute("model");

            CookiesAuthenticator authenticator = (CookiesAuthenticator) getServletContext().getAttribute("cookiesAuthenticator");
            authenticator.checkForUsernameThenDo(req, resp, () -> {
                        //onSuccess
                        String username = authenticator.getUsername(req);
                        model.switchFunction(username, functionName);
                        resp.sendRedirect(req.getContextPath() + "/program");
                    }
            );
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}