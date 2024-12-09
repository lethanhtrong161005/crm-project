package filter;

import utils.UserUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter("/*")
public class AuthFilter implements Filter {
    private static final List<String> MANAGER_URLS = Arrays.asList(
            "/users", "/profile", "/task", "/task-add",
            "/home", "/jobs", "/job-add", "/job-details", "/profile-edit"
    );
    private static final List<String> USER_URLS = Arrays.asList(
            "/home", "/profile", "/profile-edit"
    );
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        String currentPath = req.getRequestURI().substring(req.getContextPath().length());
        Cookie[] cookies = req.getCookies();
        String email = getEmailFromCookies(cookies);
        String role = getRoleFromCookies(cookies);
        boolean isStaticResource = currentPath.contains("/resources/");
        boolean isLoginRequest = currentPath.endsWith("/login");
        if(email != null){
            String fullName = UserUtils.getFullNameFromEmail(req);
            req.setAttribute("email", email);
            req.setAttribute("fullname", fullName);
        }
        if (isStaticResource || isLoginRequest) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        if (email != null && role != null) {
            if ("MANAGER".equals(role)) {
                if (currentPath.startsWith("/users") && req.getParameter("id") != null) {
                    req.setAttribute("errorMessage", "Bạn không có quyền xóa thành viên!");
                    req.getRequestDispatcher("/home").forward(req, resp);
                } else if (MANAGER_URLS.stream().anyMatch(currentPath::startsWith)) {
                    filterChain.doFilter(servletRequest, servletResponse);
                } else {
                    req.setAttribute("errorMessage", "Bạn không có quyền truy cập vào trang này!");
                    req.getRequestDispatcher("/home").forward(req, resp);
                }
            } else if ("ADMIN".equals(role)) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else if ("USER".equals(role)) {
                if (USER_URLS.stream().anyMatch(currentPath::startsWith)) {
                    filterChain.doFilter(servletRequest, servletResponse);
                } else {
                    req.setAttribute("errorMessage", "Bạn không có quyền truy cập vào trang này!");
                    req.getRequestDispatcher("/home").forward(req, resp);
                }
            } else {
                req.setAttribute("errorMessage", "Bạn không có quyền truy cập vào trang này!");
                req.getRequestDispatcher("/home").forward(req, resp);
            }
        } else {
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }
    private String getEmailFromCookies(Cookie[] cookies) {
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if ("user".equals(cookie.getName())) { // Cookie chứa email
                return cookie.getValue();
            }
        }
        return null;
    }
    private String getRoleFromCookies(Cookie[] cookies) {
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if ("roleName".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
