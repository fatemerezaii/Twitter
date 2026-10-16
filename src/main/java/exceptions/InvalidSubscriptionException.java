package exceptions;

public class InvalidSubscriptionException extends Exception {

    public InvalidSubscriptionException() {
        super("Invalid subscription type.");
    }

    public InvalidSubscriptionException(String message) {
        super(message);
    }
}