package controller;

import dto.UserTaskStatusSummaryDTO;
import services.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name="HomeController", urlPatterns = {"/home"})
public class HomeController extends HttpServlet {
    private UserService userService = new UserService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = getEmailFromCookies(req.getCookies());
        List<UserTaskStatusSummaryDTO> userTaskStatusSummaryList = userService.getUserTaskStatusSummaryByUserEmail(email);
        if(!userTaskStatusSummaryList.isEmpty()){
            req.setAttribute("userTaskStatusSummaryList", userTaskStatusSummaryList);
        }
        req.getRequestDispatcher("index.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    private String getEmailFromCookies(Cookie[] cookies) {
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if ("user".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
