package servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.CreditRequest;
import dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import user.User;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@WebServlet(name = "CreditsManagementServlet", urlPatterns = {"/api/user/credit"})
public class CreditsManagementServlet extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Check for username cookie
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
            // Parse JSON request body
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(req.getReader(), JsonObject.class);
            if (!jsonObject.has("addCredits")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Missing 'addCredits' field");
                return;
            }
            int creditsToAdd = jsonObject.get("addCredits").getAsInt();

            if (creditsToAdd <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("The credits amount must be a positive number");
                return;
            }

            // Extract username from cookie
            String username = Arrays.stream(cookies)
                    .filter(cookie -> "username".equals(cookie.getName()))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);

            if (username == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Missing or invalid username cookie");
                return;
            }

            // Retrieve user from context
            Map<String, User> users = (Map<String, User>) getServletContext().getAttribute("users");
            User user = users.get(username);
            if (user == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("User not found");
                return;
            }

            // Add credits to user
            user.addCredits(creditsToAdd);

            // Send the updated credit amount in the response
            String responseJson = gson.toJson(new UserDTO(user));
            resp.setContentType("application/json");
            resp.getWriter().write(responseJson);
        }
        else {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
