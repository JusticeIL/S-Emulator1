package servlets;

import com.google.gson.Gson;
import dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import user.User;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@WebServlet(name = "UsersServlet", urlPatterns = {"/api/users"})
public class GetAllUsersServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //get all users
        Gson gson = new Gson();
        Map<String, User> users = (Map<String, User>) getServletContext().getAttribute("users");
        String responseJson;
        synchronized (users) {
            Set<UserDTO> usersSet = users.values().stream()
                    .map(UserDTO::new)
                    .collect(Collectors.toSet());
            responseJson = gson.toJson(usersSet);
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(responseJson);
    }
}