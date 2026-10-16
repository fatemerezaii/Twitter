package exceptions;

public class AlreadyLikedException extends Exception {

    public AlreadyLikedException() {
        super("You have already liked this post.");
    }

    public AlreadyLikedException(String message) {
        super(message);
    }
}