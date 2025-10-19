package servlets.program;

import com.google.gson.Gson;
import controller.MultiUserModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.xml.bind.JAXBException;
import dto.ProgramData;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@MultipartConfig
@WebServlet(name = "ProgramServlet", urlPatterns = {"/api/program"})
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
        Collection<Part> parts = req.getParts();

        // 2. Check if the part exists
        if (parts == null || parts.size() != 1) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Error: Missing required part named 'program'.");
            return;
        }

        Part xmlPart = parts.iterator().next();
        System.out.println("Found file part with name: '" + xmlPart.getName() + "' and file name: '" + xmlPart.getSubmittedFileName() + "'");

        if (hasUsernameCookie) {
            String username = Arrays.stream(cookies)
                    .filter(cookie -> "username".equals(cookie.getName()))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);
            try {
                model.loadProgram(username, xmlPart.getInputStream());
            } catch (JAXBException e) {
                resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            } catch (IllegalArgumentException e) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write("Error: " + e.getMessage());
                return;
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}