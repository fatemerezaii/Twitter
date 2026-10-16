package model;

import java.io.Serializable;

public class Report implements Serializable {

    private static final long serialVersionUID = 1L;
    private int reportId;
    private int reporterID;
    private int reportedPostID;
    private int reportedUserID;
    private Status status;
    private String explanation;

    public enum Status {
        WAITING, CONFIRMED, REJECTED;
    }

    public Report(int reportId, String explanation, int reportedPostID, int reportedUserID, int reporterID, Status status) {
        this.reportId = reportId;
        this.explanation = explanation;
        this.reportedPostID = reportedPostID;
        this.reportedUserID = reportedUserID;
        this.reporterID = reporterID;
        this.status = status;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public int getReportedPostID() {
        return reportedPostID;
    }

    public void setReportedPostID(int reportedPostID) {
        this.reportedPostID = reportedPostID;
    }

    public int getReportedUserID() {
        return reportedUserID;
    }

    public void setReportedUserID(int reportedUserID) {
        this.reportedUserID = reportedUserID;
    }

    public int getReporterID() {
        return reporterID;
    }

    public void setReporterID(int reporterID) {
        this.reporterID = reporterID;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}