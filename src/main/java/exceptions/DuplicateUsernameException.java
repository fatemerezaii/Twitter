package exceptions;

public class DuplicateUsernameException extends Exception {

    public DuplicateUsernameException() {
        super("Username already exists.");
    }

    public DuplicateUsernameException(String message) {
        super(message);
    }
}