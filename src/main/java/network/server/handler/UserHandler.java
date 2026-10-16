package network.server.handler;

import model.Post;
import model.accounts.users.User;
import network.server.ClientHandler;
import network.server.ServerLauncher;
import network.shared.NetworkPacket;
import network.shared.RequestType;
import repository.PostRepository;
import repository.UserRepository;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class UserHandler {

    private final UserRepository repository = new UserRepository();
    private final PostRepository postRepository = new PostRepository();

    public void handle(NetworkPacket packet, ObjectOutputStream output) throws IOException {
        switch (packet.getRequestType()) {
            case BLOCK_USER:
                Object[] data = (Object[]) packet.getData();
                int id = (Integer) data[0];
                boolean blocked = (Boolean) data[1];
                boolean success = repository.blockUser(id, blocked);
                output.writeObject(new NetworkPacket(RequestType.BLOCK_USER_RESPONSE, success));
                output.flush();
                break;

            case UPDATE_PROFILE:
                updateProfile(packet, output);
                break;

            case FOLLOW_USER:
                follow(packet, output);
                break;

            case UNFOLLOW_USER:
                unfollow(packet, output);
                break;

            case GET_USERS:
                sendUsers(output);
                break;

            case GET_FOLLOWERS:
                sendFollowers(packet, output);
                break;

            case GET_FOLLOWINGS:
                sendFollowings(packet, output);
                break;

            case GET_PROFILE:
                int userId = (Integer) packet.getData();
                User user = repository.findById(userId);
                output.writeObject(new NetworkPacket(RequestType.PROFILE_RESPONSE, user));
                output.flush();
                break;

            case GET_ALL_USERS:
                sendAllUsers(output);
                break;

            case GET_USER_REPLIES:
                sendUserReplies(packet, output);
                break;

            case GET_USER_MEDIA:
                sendUserMedia(packet, output);
                break;

            default:
                break;
        }
    }

    private void updateProfile(NetworkPacket packet, ObjectOutputStream output) {
        User user = (User) packet.getData();
        boolean success = repository.updateProfile(user);
        try {
            output.writeObject(new NetworkPacket(RequestType.UPDATE_PROFILE_RESPONSE, success));
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void follow(NetworkPacket packet, ObjectOutputStream output) {
        int[] ids = (int[]) packet.getData();
        boolean success = repository.followUser(ids[0], ids[1]);
        try {
            output.writeObject(new NetworkPacket(RequestType.FOLLOW_RESPONSE, success));
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void unfollow(NetworkPacket packet, ObjectOutputStream output) {
        int[] ids = (int[]) packet.getData();
        boolean success = repository.unfollowUser(ids[0], ids[1]);
        try {
            output.writeObject(new NetworkPacket(RequestType.UNFOLLOW_RESPONSE, success));
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendUsers(ObjectOutputStream output) {
        List<User> onlineUsers = new ArrayList<>();
        synchronized (ServerLauncher.clients) {
            for (ClientHandler client : ServerLauncher.clients) {
                if (client.getUserId() == 0) {
                    continue;
                }
                User user = repository.findById(client.getUserId());
                if (user != null) {
                    onlineUsers.add(user);
                }
            }
        }

        try {
            output.writeObject(new NetworkPacket(RequestType.GET_USERS_RESPONSE, onlineUsers));
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFollowers(NetworkPacket packet, ObjectOutputStream output) {
        int userId = (Integer) packet.getData();
        List<String> followers = repository.getFollowersUsernames(userId);
        try {
            output.writeObject(new NetworkPacket(RequestType.GET_FOLLOWERS_RESPONSE, followers));
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFollowings(NetworkPacket packet, ObjectOutputStream output) {
        int userId = (Integer) packet.getData();
        List<String> followings = repository.getFollowingsUsernames(userId);
        try {
            output.writeObject(new NetworkPacket(RequestType.GET_FOLLOWINGS_RESPONSE, followings));
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendAllUsers(ObjectOutputStream output) {
        List<User> users = repository.findAll();
        try {
            output.writeObject(new NetworkPacket(RequestType.GET_ALL_USERS_RESPONSE, users));
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendUserReplies(NetworkPacket packet, ObjectOutputStream output) {
        int userId = (Integer) packet.getData();
        List<Post> replies = postRepository.findRepliesByUser(userId);
        try {
            output.writeObject(new NetworkPacket(RequestType.GET_USER_REPLIES_RESPONSE, replies));
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendUserMedia(NetworkPacket packet, ObjectOutputStream output) {
        int userId = (Integer) packet.getData();
        List<Post> media = postRepository.findMediaByUser(userId);
        try {
            output.writeObject(new NetworkPacket(RequestType.GET_USER_MEDIA_RESPONSE, media));
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}