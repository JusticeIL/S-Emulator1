package configuration;

import controller.MultiUserController;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.HashSet;

@WebListener
public class EngineInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().setAttribute("model", new MultiUserController());
        sce.getServletContext().setAttribute("users", new HashSet<String>());
        sce.getServletContext().setAttribute("cookiesAuthenticator",new CookiesAuthenticator());
    }
}