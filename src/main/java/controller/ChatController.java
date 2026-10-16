package controller;

import model.ChatMessage;
import model.accounts.users.User;
import network.NetworkManager;
import network.shared.NetworkPacket;
import network.shared.RequestType;

import java.util.ArrayList;
import java.util.List;

public class ChatController {

    private final NetworkManager network = NetworkManager.getInstance();

    public boolean connect(String host, int port) {
        return network.connect(host, port);
    }

    public boolean isConnected() {
        return network.isConnected();
    }

    public void disconnect() {
        network.disconnect();
    }

    public boolean sendMessage(ChatMessage message) {
        return network.send(new NetworkPacket(RequestType.SEND_MESSAGE, message));
    }

    public NetworkPacket request(RequestType type, Object data) {
        return network.sendAndReceive(new NetworkPacket(type, data));
    }

    public NetworkPacket listen() {
        return network.listen();
    }

    public List<User> getOnlineUsers() {

        NetworkPacket response = request(RequestType.GET_USERS, null);

        if (response != null && response.getRequestType() == RequestType.GET_USERS_RESPONSE) {
            return (List<User>) response.getData();
        }

        return new ArrayList<>();
    }

    public List<ChatMessage> loadHistory(int friendId) {

        int[] ids = {controller.manager.SessionManager.getCurrentUser().getAccID(), friendId};
        NetworkPacket response = request(RequestType.SYNC_HISTORY, ids);
        if (response != null && response.getRequestType() == RequestType.SYNC_HISTORY_RESPONSE) {
            return (List<ChatMessage>) response.getData();
        }

        return new ArrayList<>();
    }
}