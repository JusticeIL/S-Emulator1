package servlets.program;

import configuration.CookiesAuthenticator;
import controller.MultiUserModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "ExpandServlet", urlPatterns = {"/api/program/expand"})
public class ExpandServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int level = req.getParameter("level") != null ? Integer.parseInt(req.getParameter("level")) : 0;
        MultiUserModel model = (MultiUserModel) getServletContext().getAttribute("model");
        CookiesAuthenticator authenticator = (CookiesAuthenticator) getServletContext().getAttribute("cookiesAuthenticator");

        authenticator.checkForUsernameThenDo(req, resp,() -> {
            //onSuccess
            String username = authenticator.getUsername(req);
            model.Expand(username, level);

            String contextPath = req.getContextPath();
            System.out.println("Redirecting to: " + contextPath + "/api/program");

            resp.sendRedirect(req.getContextPath() + "/api/program");
        });
    }
}