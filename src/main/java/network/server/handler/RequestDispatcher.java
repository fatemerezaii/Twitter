package network.server.handler;

import model.ChatMessage;
import network.server.ClientHandler;
import network.server.ServerLauncher;
import network.shared.NetworkPacket;
import network.shared.RequestType;
import repository.MessageRepository;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class RequestDispatcher {

    private final ClientHandler clientHandler;
    private final AuthHandler authHandler = new AuthHandler();
    private final UserHandler userHandler = new UserHandler();
    private final MessageRepository messageRepository = new MessageRepository();
    private final PostHandler postHandler = new PostHandler();
    private final HashtagHandler hashtagHandler = new HashtagHandler();
    private final ReportHandler reportHandler = new ReportHandler();
    private final SubscriptionHandler subscriptionHandler = new SubscriptionHandler();

    public RequestDispatcher(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public NetworkPacket dispatch(NetworkPacket packet, ObjectOutputStream output) throws IOException {
        switch (packet.getRequestType()) {

            case SEND_MESSAGE:
                handleSendMessage(packet, output);
                break;

            case GET_ALL_USERS:
                userHandler.sendAllUsers(output);
                break;

            case SYNC_HISTORY:
                handleSyncHistory(packet, output);
                break;

            case GET_USER_REPLIES:
            case GET_USER_MEDIA:
                userHandler.handle(packet, output);
                break;

            case DELIVERED:
                handleDelivered(packet);
                break;

            case READ:
                handleRead(packet);
                break;

            case LOGIN:
            case REGISTER:
            case LOGOUT:
                authHandler.handle(packet, output, clientHandler);
                break;

            case CREATE_POST:
            case SEARCH_POSTS:
            case DELETE_POST:
            case EDIT_POST:
            case GET_POSTS:
            case LIKE_POST:
            case REPLY_POST:
            case REPLY_TO_POST:
            case GET_REPLIES:
            case VIEW_POST:
            case UNLIKE_POST:
            case SHARE_POST:
            case GET_POPULAR_POSTS:
                postHandler.handle(packet, output);
                break;

            case SEARCH_HASHTAGS:
            case GET_POPULAR_HASHTAGS:
            case SELECT_HASHTAG:
                hashtagHandler.handle(packet, output);
                break;

            case REPORT_POST:
            case REPORT_USER:
            case GET_REPORTS:
            case UPDATE_REPORT_STATUS: {
                NetworkPacket response = reportHandler.handle(packet);
                output.writeObject(response);
                output.flush();
                break;
            }

            case BUY_SUBSCRIPTION:
            case CHARGE_CREDIT:
            case CHARGE_TOKEN:
                NetworkPacket subscriptionResponse = subscriptionHandler.handle(packet);
                output.writeObject(subscriptionResponse);
                output.flush();
                break;

            case BLOCK_USER:
            case GET_PROFILE:
            case UPDATE_PROFILE:
            case FOLLOW_USER:
            case UNFOLLOW_USER:
            case GET_USERS:
            case GET_FOLLOWERS:
            case GET_FOLLOWINGS:
                userHandler.handle(packet, output);
                break;

            default:
                break;
        }
        return packet;
    }

    private void handleSendMessage(NetworkPacket packet, ObjectOutputStream output) {

        ChatMessage message = (ChatMessage) packet.getData();
        boolean success = messageRepository.add(message);
        try {
            output.writeObject(new NetworkPacket(RequestType.SEND_MESSAGE_RESPONSE, success));
            output.flush();
            if (!success) {
                return;
            }

            synchronized (ServerLauncher.clients) {
                for (ClientHandler client : ServerLauncher.clients) {
                    if (client.getUserId() == message.getReceiverId()) {
                        client.sendPacket(new NetworkPacket(RequestType.RECEIVE_MESSAGE, message));
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSyncHistory(NetworkPacket packet, ObjectOutputStream output) {
        int[] ids = (int[]) packet.getData();
        List<ChatMessage> history = messageRepository.getConversation(ids[0], ids[1]);
        try {
            output.writeObject(new NetworkPacket(RequestType.SYNC_HISTORY_RESPONSE, history));
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDelivered(NetworkPacket packet) {
        int id = (Integer) packet.getData();
        messageRepository.markAsDelivered(id);
    }

    private void handleRead(NetworkPacket packet) {
        int id = (Integer) packet.getData();
        messageRepository.markAsRead(id);
    }
}