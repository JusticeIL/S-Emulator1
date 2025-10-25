package servlets.user;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import configuration.CookiesAuthenticator;
import controller.MultiUserModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

@WebServlet(name = "UserServlet", urlPatterns = {"/api/user"})
public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CookiesAuthenticator authenticator = (CookiesAuthenticator) getServletContext().getAttribute("cookiesAuthenticator");
        authenticator.checkForUsernameThenDo(req, resp, ()->{
            //onSuccess
            String username = req.getParameter("username");

            if (username == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Missing username parameter.");
                return;
            }

            MultiUserModel model = (MultiUserModel) getServletContext().getAttribute("model");

            Gson gson = new Gson();
            String responseJson = gson.toJson(model.getUserData(username));

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(responseJson);
        });
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Expects the parameters to contain the username
        Set<String> users = (Set<String>) getServletContext().getAttribute("users");
        CookiesAuthenticator authenticator = (CookiesAuthenticator) getServletContext().getAttribute("cookiesAuthenticator");
        authenticator.checkForNoUsernameThenDo(req, () -> {
            //onSuccess
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(req.getReader(), JsonObject.class);

            String username = null;
            if (jsonObject != null && jsonObject.has("username")) {
                username = jsonObject.get("username").getAsString();
            }

            if (username == null || username.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Missing or empty username.");
                return;
            }

            synchronized (users) {
                if (users.contains(username)) {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                    resp.getWriter().write("Username " + username + " already taken.");
                } else {
                    users.add(username);
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                    resp.addCookie(new Cookie("username", username));
                    MultiUserModel model = (MultiUserModel) getServletContext().getAttribute("model");
                    model.addUser(username);
                }
            }
        }, () -> {
            //onFail
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write("Already logged in.");
        });
    }
}
