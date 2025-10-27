package servlets.execution;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import configuration.CookiesAuthenticator;
import configuration.HTTPCodes;
import controller.MultiUserModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dto.VariableDTO;

import javax.naming.InsufficientResourcesException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.InvalidParameterException;
import java.util.InputMismatchException;
import java.util.Set;

@WebServlet(name = "DebugServlet", urlPatterns = {"/api/program/debug"})
public class DebugServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Expects the query parameters to contain the arguments for the program
        // and the body to contain the breakpoints, one per line
        //expects first Item in body to be the Architecture Generation
        CookiesAuthenticator authenticator = (CookiesAuthenticator) getServletContext().getAttribute("cookiesAuthenticator");
        authenticator.checkForUsernameThenDo(req,resp,() -> {
                    //onSuccess
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(req.getReader(), JsonObject.class);
            if (!jsonObject.has("arguments") || !jsonObject.has("breakpoints") || !jsonObject.has("architectureGeneration")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Missing fields");
                return;
            }

            // Parse arguments, breakpoints and architecture generation
            Type argumentsType = new TypeToken<Set<VariableDTO>>() {}.getType();
            Set<VariableDTO> arguments = gson.fromJson(jsonObject.get("arguments"), argumentsType);

            Type breakpointsType = new TypeToken<Set<Integer>>() {}.getType();
            Set<Integer> breakpoints = gson.fromJson(jsonObject.get("breakpoints"), breakpointsType);

            String architectureGeneration = jsonObject.get("architectureGeneration").getAsString();

            // Extract username from cookie
            String username = authenticator.getUsername(req);

            MultiUserModel model = (MultiUserModel) getServletContext().getAttribute("model");
            try {
                model.startDebug(username, arguments, breakpoints, architectureGeneration);
                resp.sendRedirect(req.getContextPath() + "/api/program");
            }  catch (InvalidParameterException e) {
                resp.setStatus(HTTPCodes.UNPROCESSABLE_ENTITY);
            } catch (InsufficientResourcesException e) {
                resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            } catch (InputMismatchException e){
                resp.setStatus(HttpServletResponse.SC_PAYMENT_REQUIRED);
            }
        });
    }
}