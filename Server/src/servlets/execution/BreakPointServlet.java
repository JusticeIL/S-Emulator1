package servlets.execution;

import configuration.CookiesAuthenticator;
import controller.MultiUserModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;

@WebServlet(name = "BreakPointServlet", urlPatterns = {"/api/program/debug/breakpoint"})
public class BreakPointServlet extends HttpServlet {
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Expects the query parameters to contain the line number of the breakpoint to remove

        CookiesAuthenticator authenticator = (CookiesAuthenticator) getServletContext().getAttribute("cookiesAuthenticator");
        authenticator.checkForUsernameThenDo(req,resp, () -> {
            //onSuccess
            String username = authenticator.getUsername(req);
            int lineNumber = Integer.parseInt(req.getParameter("lineNumber"));
            MultiUserModel model = (MultiUserModel) getServletContext().getAttribute("model");
            model.removeBreakpoint(username, lineNumber);
        });
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Expects the query parameters to contain the line number of the breakpoint to add
        int lineNumber = Integer.parseInt(req.getParameter("lineNumber"));
        MultiUserModel model = (MultiUserModel) getServletContext().getAttribute("model");
        CookiesAuthenticator authenticator = (CookiesAuthenticator) getServletContext().getAttribute("cookiesAuthenticator");
        authenticator.checkForUsernameThenDo(req,resp, () -> {
            //onSuccess
            String username = authenticator.getUsername(req);
            model.addBreakpoint(username, lineNumber);
        });
    }
}
