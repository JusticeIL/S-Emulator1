package servlets.information;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import configuration.CookiesAuthenticator;
import controller.MultiUserModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;

@WebServlet(name = "SharedProgramsServlet", urlPatterns = {"/api/shared/programs"})
public class SharedProgramsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CookiesAuthenticator authenticator = (CookiesAuthenticator) getServletContext().getAttribute("cookiesAuthenticator");
        authenticator.checkForUsernameThenDo(req, resp,() -> {
            //onSuccess
            String username = authenticator.getUsername(req);

            MultiUserModel model = (MultiUserModel) getServletContext().getAttribute("model");
            Gson gson = new GsonBuilder()
                    .serializeSpecialFloatingPointValues()
                    .create();
            String responseJson = gson.toJson(model.getAllSharedProgramsData());

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(responseJson);
        });
    }
}