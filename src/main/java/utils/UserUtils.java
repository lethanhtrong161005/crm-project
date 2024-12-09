package utils;

import services.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class UserUtils {
    private static UserService userService = new UserService();
    public static String getEmailFromCookies(Cookie[] cookies) {
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if ("user".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
    public static String getFullNameFromEmail(HttpServletRequest request) {
        String email = getEmailFromCookies(request.getCookies());
        if (email != null) {
            return userService.getFullNameByEmail(email);
        }
        return null;
    }
}
