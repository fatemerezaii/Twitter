package exceptions;

public class DatabaseException extends Exception {

    public DatabaseException() {
        super("Database error.");
    }

    public DatabaseException(String message) {
        super(message);
    }
}