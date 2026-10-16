package exceptions;

public class ReportNotFoundException extends Exception {

    public ReportNotFoundException() {
        super("Report not found.");
    }

    public ReportNotFoundException(String message) {
        super(message);
    }
}