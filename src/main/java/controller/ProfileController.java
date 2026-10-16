package controller;

import exceptions.NetworkException;
import exceptions.PostNotFoundException;
import exceptions.UserNotFoundException;
import model.Post;
import model.accounts.users.User;
import network.NetworkManager;
import network.shared.NetworkPacket;
import network.shared.RequestType;

import java.util.List;
import java.util.function.Consumer;

public class ProfileController {

    public User getProfile(int userId) throws UserNotFoundException, NetworkException {
        NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.GET_PROFILE, userId));

        if (response == null) {
            throw new NetworkException("No response from server.");
        }

        if (response.getRequestType() != RequestType.PROFILE_RESPONSE) {
            throw new UserNotFoundException("User not found.");
        }

        User user = (User) response.getData();

        if (user == null) {
            throw new UserNotFoundException("User not found.");
        }

        return user;
    }

    public List<Post> getUserPosts(int userId) throws NetworkException, PostNotFoundException {
        NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.GET_POSTS, userId));

        if (response == null) {
            throw new NetworkException("No response from server.");
        }

        if (response.getRequestType() != RequestType.GET_POSTS_RESPONSE) {
            throw new PostNotFoundException("Posts not found.");
        }

        return (List<Post>) response.getData();
    }

    public List<Post> getUserReplies(int userId) throws NetworkException, PostNotFoundException {

        NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.GET_USER_REPLIES, userId));

        if (response == null) {
            throw new NetworkException("No response from server.");
        }

        if (response.getRequestType() != RequestType.GET_USER_REPLIES_RESPONSE) {
            throw new PostNotFoundException("Replies not found.");
        }

        return (List<Post>) response.getData();
    }

    public List<Post> getUserMedia(int userId) throws NetworkException, PostNotFoundException {

        NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.GET_USER_MEDIA, userId));

        if (response == null) {
            throw new NetworkException("No response from server.");
        }

        if (response.getRequestType() != RequestType.GET_USER_MEDIA_RESPONSE) {
            throw new PostNotFoundException("Media not found.");
        }

        return (List<Post>) response.getData();
    }

    public void getProfileAsync(int userId, Consumer<User> callback) {

        new Thread(() -> {
            try {
                callback.accept(getProfile(userId));
            } catch (Exception e) {
                e.printStackTrace();
                callback.accept(null);
            }
        }).start();
    }

    public void getUserPostsAsync(int userId, Consumer<List<Post>> callback) {

        new Thread(() -> {
            try {
                callback.accept(getUserPosts(userId));
            } catch (Exception e) {
                e.printStackTrace();
                callback.accept(List.of());
            }
        }).start();
    }

    public void getUserRepliesAsync(int userId, Consumer<List<Post>> callback) {

        new Thread(() -> {
            try {
                callback.accept(getUserReplies(userId));
            } catch (Exception e) {
                e.printStackTrace();
                callback.accept(List.of());
            }
        }).start();
    }

    public void getUserMediaAsync(int userId, Consumer<List<Post>> callback) {

        new Thread(() -> {
            try {
                callback.accept(getUserMedia(userId));
            } catch (Exception e) {
                e.printStackTrace();
                callback.accept(List.of());
            }
        }).start();
    }
}