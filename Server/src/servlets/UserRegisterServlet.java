package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import user.User;

import java.io.IOException;
import java.util.Map;

@WebServlet(name = "UserRegisterServlet", urlPatterns = {"/api/user/register"})
public class UserRegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Expects the parameters to contain the username

        String username = req.getParameter("username");
        Map<String, User> users = (Map<String, User>) getServletContext().getAttribute("users");

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
                if (users.containsKey(username)) {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                } else if (username == null || username.trim().isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                } else {
                    users.put(username, new User(username));
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.addCookie(new Cookie("username", username));
                }
            }
        }
    }
}
