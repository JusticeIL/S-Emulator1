package configuration;

import java.io.IOException;
import jakarta.servlet.ServletException;

@FunctionalInterface
public interface IORunnable {

    void run() throws IOException, ServletException;
}