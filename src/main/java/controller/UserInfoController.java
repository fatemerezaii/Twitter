package controller;

import controller.manager.SessionManager;
import model.accounts.users.User;
import network.NetworkManager;
import network.shared.NetworkPacket;
import network.shared.RequestType;

public class UserInfoController {

    public User getCurrentUser() {
        return SessionManager.getCurrentUser();
    }

    public String editing(String fullName, String biography, String email, String password) {

        if (fullName.isEmpty() || biography.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return "Fill fields!";
        }

        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            return "Invalid email!";
        }

        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            return "Weak password!";
        }

        if (biography.length() > 160) {
            return "Bio is long!";
        }

        User currentUser = getCurrentUser();

        if (currentUser == null) {
            return "User not found!";
        }

        currentUser.setFullName(fullName);
        currentUser.setBiography(biography);
        currentUser.setEmail(email);
        currentUser.setPassword(password);
        NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.UPDATE_PROFILE, currentUser));

        if (response != null && response.getRequestType() == RequestType.UPDATE_PROFILE_RESPONSE && (Boolean) response.getData()) {
            SessionManager.setCurrentUser(currentUser);
            return "succeed";
        }
        return "Failed!";
    }
}