package exceptions;

public class NotFollowingException extends Exception {

    public NotFollowingException() {
        super("You are not following this user.");
    }

    public NotFollowingException(String message) {
        super(message);
    }
}