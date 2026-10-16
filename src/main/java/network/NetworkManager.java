package network;

import network.client.ChatClient;
import network.shared.NetworkPacket;
import network.shared.RequestType;

import java.util.concurrent.TimeUnit;

public class NetworkManager {

    private static final NetworkManager instance = new NetworkManager();
    private final ChatClient client;

    private NetworkManager() {
        client = new ChatClient();
    }

    public static NetworkManager getInstance() {
        return instance;
    }

    public boolean connect(String host, int port) {
        return client.connect(host, port);
    }

    public boolean isConnected() {
        return client.isConnected();
    }

    public void disconnect() {

        if (client.isConnected()) {
            client.send(new NetworkPacket(RequestType.DISCONNECT, null));
            client.disconnect();
        }
    }

    public boolean send(NetworkPacket packet) {
        return client.send(packet);
    }

    public NetworkPacket listen() {
        return client.listen();
    }

    public synchronized NetworkPacket sendAndReceive(NetworkPacket packet) {
        if (!client.send(packet)) {
            return null;
        }
        RequestType responseType = getResponseType(packet.getRequestType());
        if (responseType == null) {
            return null;
        }
        return client.waitFor(responseType);
    }

    private RequestType getResponseType(RequestType request) {
        switch (request) {
            case BLOCK_USER:
                return RequestType.BLOCK_USER_RESPONSE;

            case GET_USER_REPLIES:
                return RequestType.GET_USER_REPLIES_RESPONSE;

            case GET_USER_MEDIA:
                return RequestType.GET_USER_MEDIA_RESPONSE;

            case CREATE_POST:
                return RequestType.CREATE_POST_RESPONSE;

            case EDIT_POST:
                return RequestType.EDIT_POST_RESPONSE;

            case DELETE_POST:
                return RequestType.DELETE_POST_RESPONSE;

            case LIKE_POST:
                return RequestType.LIKE_POST_RESPONSE;

            case UNLIKE_POST:
                return RequestType.UNLIKE_POST_RESPONSE;

            case VIEW_POST:
                return RequestType.VIEW_POST_RESPONSE;

            case SHARE_POST:
                return RequestType.SHARE_POST_RESPONSE;

            case SEARCH_POSTS:
                return RequestType.SEARCH_POSTS_RESPONSE;
            case LOGIN:
                return RequestType.LOGIN_SUCCESS;

            case REGISTER:
                return RequestType.REGISTER_SUCCESS;

            case GET_USERS:
                return RequestType.GET_USERS_RESPONSE;

            case GET_PROFILE:
                return RequestType.PROFILE_RESPONSE;

            case UPDATE_PROFILE:
                return RequestType.UPDATE_PROFILE_RESPONSE;

            case FOLLOW_USER:
                return RequestType.FOLLOW_RESPONSE;

            case UNFOLLOW_USER:
                return RequestType.UNFOLLOW_RESPONSE;

            case SYNC_HISTORY:
                return RequestType.SYNC_HISTORY_RESPONSE;

            case SEARCH_HASHTAGS:
                return RequestType.SEARCH_HASHTAGS_RESPONSE;

            case SEND_MESSAGE:
                return RequestType.SEND_MESSAGE_RESPONSE;

            case BUY_SUBSCRIPTION:
                return RequestType.BUY_SUBSCRIPTION_RESPONSE;

            case CHARGE_CREDIT:
                return RequestType.CHARGE_CREDIT_RESPONSE;

            case CHARGE_TOKEN:
                return RequestType.CHARGE_TOKEN_RESPONSE;

            case GET_POSTS:
                return RequestType.GET_POSTS_RESPONSE;

            case GET_ALL_USERS:
                return RequestType.GET_ALL_USERS_RESPONSE;

            case GET_POPULAR_POSTS:
                return RequestType.GET_POPULAR_POSTS_RESPONSE;

            case GET_POPULAR_HASHTAGS:
                return RequestType.GET_POPULAR_HASHTAGS_RESPONSE;

            case REPORT_POST:
                return RequestType.REPORT_POST_RESPONSE;

            case REPORT_USER:
                return RequestType.REPORT_USER_RESPONSE;

            case GET_REPORTS:
                return RequestType.GET_REPORTS_RESPONSE;

            case UPDATE_REPORT_STATUS:
                return RequestType.UPDATE_REPORT_STATUS_RESPONSE;

            default:
                return null;
        }
    }
}