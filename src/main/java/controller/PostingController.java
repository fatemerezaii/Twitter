package controller;

import controller.manager.SessionManager;
import exceptions.PostNotFoundException;
import exceptions.UserNotFoundException;
import model.accounts.users.User;
import model.Post;
import network.NetworkManager;
import network.shared.NetworkPacket;
import network.shared.RequestType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PostingController {

    private final ExecutorService threadPool = Executors.newFixedThreadPool(3);

    public interface BooleanCallback {
        void onResult(boolean success);
    }

    public User getCurrentUser() {
        return SessionManager.getCurrentUser();
    }

    public void createPostAsync(Post post, BooleanCallback callback) throws UserNotFoundException {

        if (getCurrentUser() == null) {
            throw new UserNotFoundException();
        }

        threadPool.execute(() -> {
            try {
                NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.CREATE_POST, post));
                callback.onResult(response != null && response.getRequestType() == RequestType.CREATE_POST_RESPONSE && (Boolean) response.getData());
            } catch (Exception e) {
                callback.onResult(false);
            }
        });
    }

    public void editPostAsync(Post post, BooleanCallback callback) throws PostNotFoundException {
        if (post == null) {
            throw new PostNotFoundException();
        }

        threadPool.execute(() -> {
            try {
                NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.EDIT_POST, post));
                callback.onResult(response != null && response.getRequestType() == RequestType.EDIT_POST_RESPONSE && (Boolean) response.getData());
            } catch (Exception e) {
                callback.onResult(false);
            }
        });
    }

    public void deletePostAsync(int postId, BooleanCallback callback) throws PostNotFoundException {
        if (postId <= 0) {
            throw new PostNotFoundException();
        }
        threadPool.execute(() -> {
            try {
                NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.DELETE_POST, postId));
                callback.onResult(response != null && response.getRequestType() == RequestType.DELETE_POST_RESPONSE && (Boolean) response.getData());
            } catch (Exception e) {
                callback.onResult(false);
            }
        });
    }

    public void likePostAsync(int postId, int userId, BooleanCallback callback) {
        threadPool.execute(() -> {
            try {
                int[] data = new int[]{postId, userId};
                NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.LIKE_POST, data));
                if (response != null && response.getRequestType() == RequestType.LIKE_POST_RESPONSE) {
                    callback.onResult((Boolean) response.getData());
                } else {
                    callback.onResult(false);
                }
            } catch (Exception e) {
                callback.onResult(false);
            }
        });
    }

    public Post viewPost(int postId) throws PostNotFoundException {
        NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.VIEW_POST, postId));

        if (response == null || response.getRequestType() != RequestType.VIEW_POST_RESPONSE || response.getData() == null) {
            throw new PostNotFoundException();
        }

        return (Post) response.getData();
    }

    public boolean likePost(int postId) throws UserNotFoundException {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new UserNotFoundException();
        }
        NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.LIKE_POST, new int[]{postId, currentUser.getAccID()}));
        return response != null && response.getRequestType() == RequestType.LIKE_POST_RESPONSE && (Boolean) response.getData();
    }

    public boolean unlikePost(int postId) throws UserNotFoundException {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new UserNotFoundException();
        }
        NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.UNLIKE_POST, new int[]{postId, currentUser.getAccID()}));
        return response != null && response.getRequestType() == RequestType.UNLIKE_POST_RESPONSE && (Boolean) response.getData();
    }

    public String sharePost(int postId) throws PostNotFoundException {
        NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.SHARE_POST, postId));
        if (response == null || response.getRequestType() != RequestType.SHARE_POST_RESPONSE) {
            throw new PostNotFoundException();
        }
        return (String) response.getData();
    }
}