package controller;

import controller.manager.SessionManager;
import model.accounts.users.User;
import network.NetworkManager;
import network.shared.NetworkPacket;
import network.shared.RequestType;

import java.util.ArrayList;
import java.util.List;

public class UsersDataController {

    public User getCurrentUser() {
        return SessionManager.getCurrentUser();
    }
    public List<User> searchUsers(String text) {
        List<User> result = new ArrayList<>();
        NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.GET_ALL_USERS, null));

        if (response == null || response.getRequestType() != RequestType.GET_ALL_USERS_RESPONSE) {
            return result;
        }

        List<User> users = (List<User>) response.getData();

        if (text == null) {
            text = "";
        }

        String keyword = text.toLowerCase().trim();

        for (User user : users) {
            if ((user.getUsername() != null && user.getUsername().toLowerCase().contains(keyword)) || (user.getFullName() != null && user.getFullName().toLowerCase().contains(keyword))) {
                result.add(user);
            }
        }
        return result;
    }

    public User getUser(int userId) {
        NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.GET_ALL_USERS, null));

        if (response == null || response.getRequestType() != RequestType.GET_ALL_USERS_RESPONSE) {
            return null;
        }

        List<User> users = (List<User>) response.getData();

        for (User user : users) {
            if (user.getAccID() == userId) {
                return user;
            }
        }

        return null;
    }

    public void removeFavoriteHashtag(int hashtagId) {
        getCurrentUser().getFavoriteHashtags().remove(Integer.valueOf(hashtagId));
    }
}