package exceptions;

public class InsufficientCreditException extends Exception {

    public InsufficientCreditException() {
        super("Not enough credit.");
    }

    public InsufficientCreditException(String message) {
        super(message);
    }
}