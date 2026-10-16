package exceptions;

public class NetworkException extends Exception {

    public NetworkException() {
        super("Network error!");
    }

    public NetworkException(String message) {
        super(message);
    }

    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}