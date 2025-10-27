package configuration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;

public class CookiesAuthenticator {

    public void checkForUsernameThenDo(HttpServletRequest req,HttpServletResponse resp, IORunnable onSuccess, IORunnable onFail) throws ServletException, IOException {
        if (hasUsernameCookie(req)) {
            onSuccess.run();
        } else {
            onFail.run();
        }
    }

    public void checkForUsernameThenDo(HttpServletRequest req,HttpServletResponse resp, IORunnable onSuccess) throws ServletException, IOException {
        checkForUsernameThenDo(req,resp,onSuccess,()->{
            //onFail
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("Not logged in.");
        });
    }

    public void checkForNoUsernameThenDo(HttpServletRequest req,IORunnable onSuccess, IORunnable onFail) throws ServletException, IOException {
        if (!hasUsernameCookie(req)) {
            onSuccess.run();
        } else {
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

    public String getUsername(HttpServletRequest req){
        Cookie[] cookies = req.getCookies();
        return Arrays.stream(cookies)
                .filter(cookie -> "username".equals(cookie.getName()))
                .findFirst()
                .map(cookie -> cookie.getValue().replace('_', ' '))
                .orElse(null);
    }

}