package configuration;

import controller.MultiUserModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.BiConsumer;

public class CookiesAuthenticator {

    public void checkForUsernameThenDo(HttpServletRequest req, HttpServletResponse resp, BiConsumer<HttpServletRequest,HttpServletResponse> consumer) throws ServletException, IOException {
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
            consumer.accept(req,resp);
        }else{
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    public void checkForNoUsernameThenDo(HttpServletRequest req, HttpServletResponse resp, BiConsumer<HttpServletRequest,HttpServletResponse> consumer) throws ServletException, IOException {
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
        if (!hasUsernameCookie) {
            consumer.accept(req,resp);
        }else{
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }


}
