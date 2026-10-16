package exceptions;

public class AlreadyFollowingException extends Exception {

    public AlreadyFollowingException() {
        super("You are already following this user.");
    }

    public AlreadyFollowingException(String message) {
        super(message);
    }
}