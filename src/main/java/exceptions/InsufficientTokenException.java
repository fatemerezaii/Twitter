package exceptions;

public class InsufficientTokenException extends Exception {

    public InsufficientTokenException() {
        super("Not enough tokens.");
    }

    public InsufficientTokenException(String message) {
        super(message);
    }
}