package servlets;

import controller.MultiUserModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;

@WebServlet(name = "FunctionSwitchServlet", urlPatterns = {"/program/function"})
public class FunctionSwitchServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Expects the query parameters to contain the function name to switch to
        String functionName = req.getParameter("name");
        if (functionName != null) {
            // Switch to the specified function
            MultiUserModel model = (MultiUserModel) getServletContext().getAttribute("model");

            Cookie[] cookies = req.getCookies();
            boolean hasUsernameCookie = false;
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("username".equals(cookie.getName())) {
                        hasUsernameCookie = true;
                        break;
                    }
                }
            }
            if (hasUsernameCookie) {
                String username = Arrays.stream(cookies)
                        .filter(cookie -> "username".equals(cookie.getName()))
                        .findFirst()
                        .map(Cookie::getValue)
                        .orElse(null);
                model.switchFunction(username, functionName);
                 resp.sendRedirect(req.getContextPath() + "/program");}else{
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
