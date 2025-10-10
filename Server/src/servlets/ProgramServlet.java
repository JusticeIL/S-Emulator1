package servlets;

import com.google.gson.Gson;
import controller.MultiUserModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.bind.JAXBException;
import dto.ProgramData;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@WebServlet(name = "ProgramServlet", urlPatterns = {"/program"})
public class ProgramServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
            Gson gson = new Gson();
            Optional<ProgramData> data = model.getProgramData(username);
            data.ifPresentOrElse(
                    programData -> {
                        String responseJson = gson.toJson(programData);
                        resp.setContentType("application/json");
                        resp.setCharacterEncoding("UTF-8");
                        try {
                            resp.getWriter().write(responseJson);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    () -> {
                        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    }
            );
        } else {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Expects the body to contain the path to the program xml file

        String sprogramPath = req.getReader().readLine();
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
        try {
            model.loadProgram(username, sprogramPath);
            doGet(req, resp);
        } catch (JAXBException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }}else{
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

}
