package controller;

import exceptions.NetworkException;
import exceptions.ReportNotFoundException;
import exceptions.UserNotFoundException;
import model.Report;
import model.accounts.users.User;
import network.NetworkManager;
import network.shared.NetworkPacket;
import network.shared.RequestType;

import java.util.ArrayList;
import java.util.List;

public class ReportController {

    public String createReport(int reporterId, int reportedPostId, int reportedUserId, String explanation) throws ReportNotFoundException, NetworkException {

        if (reportedPostId <= 0 && reportedUserId <= 0) {
            throw new ReportNotFoundException("Nothing selected to report.");
        }

        Report report = new Report(0, explanation, reportedPostId, reportedUserId, reporterId, Report.Status.WAITING);
        RequestType type = reportedPostId > 0 ? RequestType.REPORT_POST : RequestType.REPORT_USER;
        NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(type, report));

        if (response == null) {
            throw new NetworkException("No response from server.");
        }

        if ((Boolean) response.getData()) {
            return "Succeed!";
        }

        throw new ReportNotFoundException("Failed to submit report.");
    }

    public String confirmReport(Report report, boolean confirm) throws ReportNotFoundException, NetworkException {

        if (report == null) {
            throw new ReportNotFoundException("Report not found.");
        }

        report.setStatus(confirm ? Report.Status.CONFIRMED : Report.Status.REJECTED);
        NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.UPDATE_REPORT_STATUS, report));

        if (response == null) {
            throw new NetworkException("No response from server.");
        }

        if (response.getRequestType() == RequestType.UPDATE_REPORT_STATUS_RESPONSE && (Boolean) response.getData()) {
            return confirm ? "Confirmed" : "Rejected";
        }

        throw new ReportNotFoundException("Failed to update report.");
    }

    public List<Report> pendingReports() throws NetworkException {

        NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.GET_REPORTS, null));

        if (response == null) {
            throw new NetworkException("No response from server.");
        }

        List<Report> pending = new ArrayList<>();

        if (response.getRequestType() == RequestType.GET_REPORTS_RESPONSE) {
            List<Report> reports = (List<Report>) response.getData();
            for (Report report : reports) {
                if (report.getStatus() == Report.Status.WAITING) {
                    pending.add(report);
                }
            }
        }
        return pending;
    }

    public User getUserById(int userId) throws UserNotFoundException, NetworkException {
        NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.GET_PROFILE, userId));

        if (response == null) {
            throw new NetworkException("No response from server.");
        }

        if (response.getRequestType() != RequestType.PROFILE_RESPONSE) {
            throw new UserNotFoundException("User not found.");
        }

        User user = (User) response.getData();

        if (user == null) {
            throw new UserNotFoundException("User not found.");
        }

        return user;
    }
}