package exceptions;

public class NetworkConnectionException extends Exception {

    public NetworkConnectionException() {
        super("Network connection failed.");
    }

    public NetworkConnectionException(String message) {
        super(message);
    }
}