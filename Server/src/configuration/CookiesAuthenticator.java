package configuration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class CookiesAuthenticator {

    public void checkForUsernameThenDo(HttpServletRequest req, IORunnable onSuccess, IORunnable onFail) throws ServletException, IOException {
        if (hasUsernameCookie(req)) {
            onSuccess.run();
        }else{
            onFail.run();
        }
    }

    public void checkForNoUsernameThenDo(HttpServletRequest req,IORunnable onSuccess, IORunnable onFail) throws ServletException, IOException {
        if (!hasUsernameCookie(req)) {
            onSuccess.run();
        }else{
            onFail.run();
        }
    }

    private boolean hasUsernameCookie(HttpServletRequest req){
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
        return hasUsernameCookie;
    }

}
