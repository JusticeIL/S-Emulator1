package servlets;

import com.google.gson.Gson;
import controller.MultiUserModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import program.data.VariableDTO;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@WebServlet(name = "ProgramExecutionServlet", urlPatterns = {"/program/execute"})
public class ProgramExecutionServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        MultiUserModel model = (MultiUserModel) getServletContext().getAttribute("model");
        // Expects the query parameters to contain the arguments for the program

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

            List<String> argNames = model.getProgramData(username).get().getProgramXArguments();
            Set<VariableDTO> args = argNames.stream().map(name -> new VariableDTO(name, Integer.parseInt(req.getParameter(name)))).collect(Collectors.toSet());
            model.runProgram(username, args);
            resp.sendRedirect(req.getContextPath() + "/program");
        } else {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
