package repository;

import config.MysqlConfig;
import dto.JobDTO;
import dto.JobStatusPercentageDTO;
import entities.JobEntity;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class JobRepository {
    public List<JobEntity> findAll(){
        List<JobEntity> listJob = new ArrayList<JobEntity>();
        String query = "SELECT * FROM jobs";
        Connection connection = MysqlConfig.getConnection();
        try{
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            while(result.next()) {
                JobEntity job = new JobEntity();
                job.setId(result.getInt("id"));
                job.setName(result.getString("name"));
                job.setStart_date(result.getDate("start_date"));
                job.setEnd_date(result.getDate("end_date"));
                listJob.add(job);
            }

        }catch (Exception e) {
            System.out.println("findAll: " + e.getMessage());
        }
        return listJob;
    }

    public int deleteById(int id) {
        int rowDeleted = 0;
        String query = "DELETE FROM jobs j WHERE j.id = ?";
        Connection connection = MysqlConfig.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);

            rowDeleted = statement.executeUpdate();


        }catch (Exception e) {
            System.out.println("deleteById : " + e.getMessage());
        }
        return rowDeleted;
    }

    public int insert(String jobName, Date startDate, Date endDate) {
        int rowInserted = 0;
        String query = "INSERT INTO jobs(name,start_date,end_date)VALUES(?,?,?)";
        Connection connection = MysqlConfig.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, jobName);
            statement.setDate(2, startDate);
            statement.setDate(3, endDate);

            rowInserted = statement.executeUpdate();

        }catch (Exception e) {
            System.out.println("insert : " + e.getMessage());
        }
        return rowInserted;
    }
    public List<JobStatusPercentageDTO> findJobStatusPercentageById(int id){
        String query = "SELECT s.name AS status_name, " +
                "COUNT(t.id) AS task_count, " +
                "(COUNT(t.id) / (SELECT COUNT(*) FROM tasks WHERE job_id = ?)) * 100 AS percentage " +
                "FROM status s " +
                "JOIN tasks t ON t.status_id = s.id " +
                "WHERE t.job_id = ? " +
                "GROUP BY s.id " +
                "ORDER BY percentage DESC";
        List<JobStatusPercentageDTO> result = new ArrayList<>();
        try (Connection connection = MysqlConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);
            statement.setInt(2, id);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                JobStatusPercentageDTO status = new JobStatusPercentageDTO();
                status.setStatusName(resultSet.getString("status_name"));
                status.setPercentage(resultSet.getDouble("percentage"));
                result.add(status);
            }
        } catch (Exception e) {
            System.out.println("findJobStatusPercentage : " + e.getMessage());
        }
        return result;
    }
    public List<JobDTO> findTaskDetailsByJobAndStatusById(int id) {
        String query = "SELECT j.name AS job_name, s.name AS status_name, u.fullname AS user_name, t.name AS task_name, t.start_date, t.end_date " +
                "FROM jobs j " +
                "LEFT JOIN tasks t ON j.id = t.job_id " +
                "LEFT JOIN users u ON t.user_id = u.id " +
                "LEFT JOIN status s ON t.status_id = s.id " +
                "WHERE j.id = ? " +
                "ORDER BY j.name, s.name, u.fullname;";

        List<JobDTO> result = new ArrayList<>();
        try (Connection connection = MysqlConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                String jobName = rs.getString("job_name");
                String statusName = rs.getString("status_name");
                String userName = rs.getString("user_name");
                String taskName = rs.getString("task_name");
                Date startDate = rs.getDate("start_date");
                Date endDate = rs.getDate("end_date");
                JobDTO jobDTO = new JobDTO(jobName, statusName, userName, taskName, startDate, endDate);
                result.add(jobDTO);
            }
        } catch (Exception e) {
            System.out.println("findTaskDetailsByJobAndStatus : " + e.getMessage());
        }
        return result;
    }

}
