package controller;

import controller.manager.SessionManager;
import javafx.application.Platform;
import model.accounts.users.NormalUser;
import model.accounts.users.User;
import network.NetworkManager;
import network.shared.NetworkPacket;
import network.shared.RequestType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EntryController {

    private final ExecutorService threadPool = Executors.newFixedThreadPool(2);

    public interface EntryCallback {
        void onResult(String errorMessage, User user, boolean isAdmin);
    }

    public void signUp(String fullName, String username, String email, String phoneNumber, String password, String confirmPass, Date birthdayDate, EntryCallback callback) {

        try {
            validateSignup(fullName, username, email, phoneNumber, password, confirmPass, birthdayDate);
        } catch (IllegalArgumentException e) {
            callback.onResult(e.getMessage(), null, false);
            return;
        }

        threadPool.execute(() -> {
            try {
                if (!NetworkManager.getInstance().isConnected()) {
                    NetworkManager.getInstance().connect("localhost", 8080);
                }
                Date registerDate = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date dateOnly = formatter.parse(formatter.format(registerDate));
                NormalUser newUser = new NormalUser(0, fullName, phoneNumber, username, birthdayDate, null, password, email, dateOnly, false, 0, 3000, null, false, null);
                NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.REGISTER, newUser));
                Platform.runLater(() -> {
                    if (response == null) {
                        callback.onResult("No response from server!", null, false);
                        return;
                    }

                    if (response.getRequestType() == RequestType.REGISTER_SUCCESS) {
                        User registeredUser = (User) response.getData();
                        SessionManager.setCurrentUser(registeredUser);
                        callback.onResult(null, registeredUser, false);
                    } else {
                        callback.onResult((String) response.getData(), null, false);
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> callback.onResult(e.getMessage(), null, false));
            }
        });
    }

    public void loginUser(String username, String password, EntryCallback callback) {
        try {
            validateLogin(username, password);
        } catch (IllegalArgumentException e) {
            callback.onResult(e.getMessage(), null, false);
            return;
        }

        threadPool.execute(() -> {
            try {
                if (!NetworkManager.getInstance().isConnected()) {
                    NetworkManager.getInstance().connect("localhost", 8080);
                }
                User loginUser = new NormalUser();
                loginUser.setUsername(username);
                loginUser.setPassword(password);
                NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.LOGIN, loginUser));
                Platform.runLater(() -> {
                    if (response == null) {
                        callback.onResult("Connection timeout!", null, false);
                        return;
                    }

                    if (response.getRequestType() == RequestType.LOGIN_SUCCESS) {
                        User user = (User) response.getData();
                        if (user.isBlocked()) {
                            callback.onResult("Blocked", null, false);

                        } else {
                            SessionManager.setCurrentUser(user);
                            callback.onResult(null, user, user.isAdmin());
                        }

                    } else {
                        callback.onResult((String) response.getData(), null, false);
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> callback.onResult(e.getMessage(), null, false));
            }
        });
    }

    private void validateSignup(String fullName, String username, String email, String phoneNumber, String password, String confirmPass, Date birthdayDate) {

        if (fullName.isBlank() || username.isBlank() || email.isBlank() || phoneNumber.isBlank() || password.isBlank() || confirmPass.isBlank() || birthdayDate == null) {
            throw new IllegalArgumentException("Fill all fields!");
        }

        if (!phoneNumber.matches("^09\\d{9}$")) {
            throw new IllegalArgumentException("Invalid phone number!");
        }

        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email!");
        }

        if (!password.equals(confirmPass)) {
            throw new IllegalArgumentException("Passwords do not match!");
        }

        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            throw new IllegalArgumentException("Weak password!");
        }

    }

    private void validateLogin(String username, String password) {

        if (username.isBlank() || password.isBlank()) {
            throw new IllegalArgumentException("Fill all fields!");
        }

    }

}