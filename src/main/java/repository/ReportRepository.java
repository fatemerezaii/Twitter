package repository;

import database.DatabaseManager;
import interfaces.IRepository;
import model.Report;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportRepository implements IRepository<Report> {

    @Override
    public boolean add(Report report) {
        return save(report);
    }

    public boolean save(Report report) {
        String query = """
                INSERT INTO Reports
                (reporterID, reportedPostID, reportedUserID, status, explanation)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, report.getReporterID());
            if (report.getReportedPostID() > 0) {
                stmt.setInt(2, report.getReportedPostID());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            if (report.getReportedUserID() > 0) {
                stmt.setInt(3, report.getReportedUserID());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setString(4, report.getStatus().name());
            stmt.setString(5, report.getExplanation());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    report.setReportId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Report report) {
        String query = """
                UPDATE Reports
                SET status = ?, explanation = ?
                WHERE reportID = ?
                """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, report.getStatus().name());
            stmt.setString(2, report.getExplanation());
            stmt.setInt(3, report.getReportId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStatus(int reportId, Report.Status newStatus) {
        String query = """
                UPDATE Reports
                SET status = ?
                WHERE reportID = ?
                """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newStatus.name());
            stmt.setInt(2, reportId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String query = "DELETE FROM Reports WHERE reportID=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Report findById(int id) {
        String query = "SELECT * FROM Reports WHERE reportID=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Report> findAll() {
        List<Report> reports = new ArrayList<>();
        String query = "SELECT * FROM Reports ORDER BY reportID DESC";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                reports.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reports;
    }

    private Report mapResultSet(ResultSet rs) throws SQLException {
        return new Report(rs.getInt("reportID"), rs.getString("explanation"), rs.getInt("reportedPostID"), rs.getInt("reportedUserID"), rs.getInt("reporterID"), Report.Status.valueOf(rs.getString("status")));
    }
}