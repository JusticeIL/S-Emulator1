package servlets.program;

import controller.MultiUserModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;

@WebServlet(name = "ActiveProgramServlet", value = "/api/program/active")
public class ActiveProgramServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //expects query param "programName"
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
            String programName = req.getParameter("programName");
            model.setActiveProgram(username, programName);
            resp.sendRedirect(req.getContextPath() + "/api/program");
        }else{
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
