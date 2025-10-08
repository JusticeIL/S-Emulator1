package servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

@WebServlet(name = "UserServlet", urlPatterns = {"/user"})
public class UserServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Expects the parameters to contain the username

        String username = req.getParameter("username");
        Set<String> users = (Set<String>) getServletContext().getAttribute("users");

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
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            synchronized (users) {
                if (users.contains(username) || username == null || username.trim().isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                } else {
                    users.add(username);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.addCookie(new Cookie("username", username));
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //get all users
        Gson gson = new Gson();
        Set<String> users = (Set<String>) getServletContext().getAttribute("users");
        String responseJson;
        synchronized (users) {
            responseJson = gson.toJson(users);
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(responseJson);
    }
}
