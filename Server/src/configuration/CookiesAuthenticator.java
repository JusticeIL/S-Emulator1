package configuration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.function.BiConsumer;

public class CookiesAuthenticator {

    public void checkForUsernameThenDo(HttpServletRequest req, HttpServletResponse resp,String massage, BiConsumer<HttpServletRequest,HttpServletResponse> consumer) throws ServletException, IOException {
        if (hasUsernameCookie(req)) {
            consumer.accept(req,resp);
        }else{
            addMassageForDeniedAccessToResponse(resp,massage);
        }
    }

    public void checkForNoUsernameThenDo(HttpServletRequest req, HttpServletResponse resp,String massage,BiConsumer<HttpServletRequest,HttpServletResponse> consumer) throws ServletException, IOException {
        if (!hasUsernameCookie(req)) {
            consumer.accept(req,resp);
        }else{
            addMassageForDeniedAccessToResponse(resp,massage);
        }
    }

    public void checkForUsernameThenDo(HttpServletRequest req, HttpServletResponse resp, BiConsumer<HttpServletRequest,HttpServletResponse> consumer) throws ServletException, IOException {
        checkForUsernameThenDo(req,resp,null,consumer);
    }

    public void checkForNoUsernameThenDo(HttpServletRequest req, HttpServletResponse resp,BiConsumer<HttpServletRequest,HttpServletResponse> consumer) throws ServletException, IOException {
        checkForNoUsernameThenDo(req,resp,null,consumer);
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

    private void addMassageForDeniedAccessToResponse(HttpServletResponse resp,String messageForDeniedAccess) throws IOException {
        resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        if (messageForDeniedAccess != null) {
            resp.getWriter().write(messageForDeniedAccess);
        }
    }
}
