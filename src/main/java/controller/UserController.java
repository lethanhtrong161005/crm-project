package controller;

import dto.UserTaskStatusDTO;
import entities.RolesEntity;
import entities.TaskEntity;
import entities.UsersEntity;
import services.TaskService;
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

@WebServlet(name="UserController", urlPatterns = {"/users", "/user-add", "/user-details", "/profile", "/profile-edit"})
public class UserController extends HttpServlet {
    private UserService service = new UserService();
    private TaskService taskService = new TaskService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if(path.endsWith("/users")) {
            loadUsers(req, resp);
        }else if(path.endsWith("/user-add")) {
            addUser(req, resp);
        }else if(path.endsWith("/user-details")) {
            loadUserDetail(req, resp);
        }else if(path.endsWith("/profile")) {
            loadProfile(req, resp);
        }else if(path.endsWith("/profile-edit")) {
            loadProfileEdit(req, resp);
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if(path.endsWith("/user-add")) {
            addUserPost(req, resp);
        }else if(path.endsWith("/profile-edit")) {
            updateStatusProfilePost(req, resp);
        }
    }
    private void loadUsers(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        if(id != null) {
            service.deleteUserById(Integer.parseInt(id));
        }
        List<UsersEntity> listUser = new ArrayList<UsersEntity>();
        listUser = service.getAllUser();
        req.setAttribute("listUser", listUser);

        req.getRequestDispatcher("user-table.jsp").forward(req, resp);
    }
    private void addUserPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String fullname = req.getParameter("fullname");
        int roleId = Integer.parseInt(req.getParameter("role") != null ? req.getParameter("role") : "0");
        service.insertUser(email, password, fullname, roleId);
        List<RolesEntity> listRole = service.getAllRole();

        req.setAttribute("listRole", listRole);

        req.getRequestDispatcher("user-add.jsp").forward(req, resp);
    }
    private void addUser(HttpServletRequest req, HttpServletResponse resp)  throws ServletException, IOException{

        List<RolesEntity> listRole = service.getAllRole();

        req.setAttribute("listRole", listRole);

        req.getRequestDispatcher("user-add.jsp").forward(req, resp);
    }

    private void loadUserDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        UsersEntity user = service.getUserById(id);
        req.setAttribute("user", user);
        List<String> status = new ArrayList<>();
        status.add("Chưa thực hiện");
        status.add("Đang thực hiện");
        status.add("Đã hoàn thành");
        if(taskService.getTaskByUserIdAndStatusName(id, status.get(0)).size() > 0){
            req.setAttribute("status1", taskService.getTaskByUserIdAndStatusName(id, status.get(0)));
        }
        if(taskService.getTaskByUserIdAndStatusName(id, status.get(1)).size() > 0){
            req.setAttribute("status2", taskService.getTaskByUserIdAndStatusName(id, status.get(1)));
        }
        if(taskService.getTaskByUserIdAndStatusName(id, status.get(2)).size() > 0){
            req.setAttribute("status3", taskService.getTaskByUserIdAndStatusName(id, status.get(2)));
        }
        UserTaskStatusDTO userTaskStatusDTO = service.getUserTaskCompletionPercentageByUserId(id);
        if(userTaskStatusDTO != null){
            req.setAttribute("notStartedPercentage", userTaskStatusDTO.getNotStartedPercentage());
            req.setAttribute("inProgressPercentage", userTaskStatusDTO.getInProgressPercentage());
            req.setAttribute("completedPercentage", userTaskStatusDTO.getCompletedPercentage());
        }
        req.getRequestDispatcher("user-details.jsp").forward(req, resp);
    }

    private void loadProfile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = getEmailFromCookies(req.getCookies());
        UserTaskStatusDTO userTaskStatusDTO = service.getgetUserTaskCompletionPercentageByUserEmail(email);
        if(userTaskStatusDTO != null){
            req.setAttribute("notStartedPercentage", userTaskStatusDTO.getNotStartedPercentage());
            req.setAttribute("inProgressPercentage", userTaskStatusDTO.getInProgressPercentage());
            req.setAttribute("completedPercentage", userTaskStatusDTO.getCompletedPercentage());
        }
        List<TaskEntity> tasks = taskService.getAllTaskByEmail(email);
        req.setAttribute("tasks", tasks);
        req.getRequestDispatcher("profile.jsp").forward(req, resp);
    }
    private void loadProfileEdit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        System.out.println("task_id : " + id);
        TaskEntity task = taskService.getTaskById(id);
        if(task != null){
            req.setAttribute("task", task);
        }else{
            req.setAttribute("task", null);
        }
        req.getRequestDispatcher("profile-edit.jsp").forward(req, resp);
    }
    private void updateStatusProfilePost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        String statusIdStr = req.getParameter("status");
        String taskIdStr = req.getParameter("id");
        boolean isUpdated = taskService.updateTaskStatus(taskIdStr, statusIdStr);
        if(isUpdated){
            resp.sendRedirect(req.getContextPath() + "/profile");
        }else {
            req.setAttribute("errorMessage", "Không thể cập nhật trạng thái công việc.");
            req.getRequestDispatcher("profile-edit.jsp").forward(req, resp);
        }
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
