package controller;

import controller.manager.SessionManager;
import model.accounts.users.User;
import network.NetworkManager;
import network.shared.NetworkPacket;
import network.shared.RequestType;

import java.util.ArrayList;
import java.util.List;

public class UserConnectionController {

    public User getCurrentUser() {
        return SessionManager.getCurrentUser();
    }

    public boolean follow(int followerId, int followedId) {
        NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.FOLLOW_USER, new int[]{followerId, followedId}));
        return response != null && response.getRequestType() == RequestType.FOLLOW_RESPONSE && (Boolean) response.getData();
    }

    public boolean unfollow(int followerId, int followedId) {
        NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.UNFOLLOW_USER, new int[]{followerId, followedId}));
        return response != null && response.getRequestType() == RequestType.UNFOLLOW_RESPONSE && (Boolean) response.getData();
    }
}