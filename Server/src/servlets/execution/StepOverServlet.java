package servlets.execution;

import configuration.CookiesAuthenticator;
import configuration.HTTPCodes;
import controller.MultiUserModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InsufficientResourcesException;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.InputMismatchException;

@WebServlet(name = "StepOverServlet", urlPatterns = {"/api/program/debug/stepover"})
public class StepOverServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CookiesAuthenticator authenticator = (CookiesAuthenticator) getServletContext().getAttribute("cookiesAuthenticator");
        authenticator.checkForUsernameThenDo(req, resp, () -> {
            //onSuccess
            String username = authenticator.getUsername(req);
            MultiUserModel model = (MultiUserModel) getServletContext().getAttribute("model");
            try {
                model.stepOver(username);
                resp.sendRedirect(req.getContextPath() + "/api/program");
            } catch (InvalidParameterException e) {
                resp.setStatus(HTTPCodes.UNPROCESSABLE_ENTITY);
            } catch (InsufficientResourcesException e) {
                resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            } catch (InputMismatchException e) {
                resp.setStatus(HttpServletResponse.SC_PAYMENT_REQUIRED);
            }
        });
    }
}