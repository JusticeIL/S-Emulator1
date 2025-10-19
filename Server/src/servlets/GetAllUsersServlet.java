package servlets;

import com.google.gson.Gson;
import controller.MultiUserModel;
import dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

@WebServlet(name = "UsersServlet", urlPatterns = {"/api/users"})
public class GetAllUsersServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //get all users
        Gson gson = new Gson();
        Set<String> users = (Set<String>) getServletContext().getAttribute("users");
        MultiUserModel model = (MultiUserModel) getServletContext().getAttribute("model");
        String responseJson;
        synchronized (users) {
            Set<UserDTO> usersSet = model.getAllUsers();
            responseJson = gson.toJson(usersSet);
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(responseJson);
    }
}