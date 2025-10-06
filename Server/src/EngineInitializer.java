import controller.SingleProgramController;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.HashSet;
import java.util.Set;

@WebListener
public class EngineInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().setAttribute("model", new SingleProgramController());
        sce.getServletContext().setAttribute("users", new HashSet<String>());
    }
}