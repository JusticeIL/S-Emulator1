package servlets.user;

import com.google.gson.Gson;
import controller.MultiUserModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

@WebServlet(name = "UserServlet", urlPatterns = {"/api/user"})
public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

            Set<String> users = (Set<String>) getServletContext().getAttribute("users");
            MultiUserModel model = (MultiUserModel) getServletContext().getAttribute("model");

            Gson gson = new Gson();
            String responseJson = gson.toJson(model.getUserData(username));

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(responseJson);
        } else {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write("Not logged in.");
        }
    }


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
            resp.getWriter().write("Already logged in.");
        } else {
            synchronized (users) {
                if (users.contains(username)) {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                    resp.getWriter().write("Username " + username + " already taken.");
                } else if (username == null || username.trim().isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("Missing or empty username.");
                } else {
                    users.add(username);
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                    resp.addCookie(new Cookie("username", username));
                    MultiUserModel model = (MultiUserModel ) getServletContext().getAttribute("model");
                    model.addUser(username);
                }
            }
        }
    }
}
