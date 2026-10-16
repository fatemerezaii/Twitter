package controller;

import controller.manager.SessionManager;
import model.accounts.users.User;
import model.Post;
import network.NetworkManager;
import network.shared.NetworkPacket;
import network.shared.RequestType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PostAlgorithmsController {

    private final ExecutorService threadPool = Executors.newFixedThreadPool(2);

    public interface PostFetchCallback {
        void onResult(List<Post> posts, String error);
    }

    public User getCurrentUser() {
        return SessionManager.getCurrentUser();
    }

    private void fetchAllPosts(PostFetchCallback callback) {
        threadPool.execute(() -> {
            try {
                if (!NetworkManager.getInstance().isConnected()) {
                    NetworkManager.getInstance().connect("localhost", 8080);
                }

                NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.GET_POSTS, null));

                if (response != null && response.getRequestType() == RequestType.GET_POSTS_RESPONSE) {
                    @SuppressWarnings("unchecked")
                    List<Post> posts = (List<Post>) response.getData();
                    callback.onResult(posts, null);
                } else {
                    callback.onResult(null, "Failed to load posts from server.");
                }
            } catch (Exception e) {
                callback.onResult(null, "Network error: " + e.getMessage());
            }
        });
    }

    public void getSuggestedPostsForUserAsync(User user, int limit, PostFetchCallback callback) {
        if (user == null || limit <= 0) {
            callback.onResult(new ArrayList<>(), null);
            return;
        }

        fetchAllPosts((allPosts, error) -> {
            if (error != null) {
                callback.onResult(null, error);
                return;
            }

            List<Post> suggested = new ArrayList<>();
            List<Post> followingPosts = new ArrayList<>();
            List<Post> hashtagPosts = new ArrayList<>();
            List<Post> popularPosts = new ArrayList<>();

            for (Post p : allPosts) {
                if (p == null || p.isDeleted() || p.isLocked()) {
                    continue;
                }
                if (user.getFollowings().contains(p.getPosterID())) {
                    followingPosts.add(p);
                }

                boolean hasFavHashtag = false;
                if (user.getFavoriteHashtags() != null) {
                    for (int hid : user.getFavoriteHashtags()) {
                        if (p.getHashtags() != null && p.getHashtags().contains(hid)) {
                            hasFavHashtag = true;
                            break;
                        }
                    }
                }

                if (hasFavHashtag && !followingPosts.contains(p)) {
                    hashtagPosts.add(p);
                }

                popularPosts.add(p);
            }

            followingPosts.sort((p1, p2) -> p2.getPostDate().compareTo(p1.getPostDate()));
            hashtagPosts.sort((p1, p2) -> p2.getPostDate().compareTo(p1.getPostDate()));
            popularPosts.sort((p1, p2) -> Integer.compare(p2.getLikes(), p1.getLikes()));

            int followingLimit = Math.min(limit / 2, followingPosts.size());
            int hashtagLimit = Math.min(limit * 3 / 10, hashtagPosts.size());

            for (Post p : followingPosts) {
                if (suggested.size() >= followingLimit) {
                    break;
                }
                if (!suggested.contains(p)){
                    suggested.add(p);
                }
            }
            for (Post p : hashtagPosts) {
                if (suggested.size() >= followingLimit + hashtagLimit){
                    break;
                }
                if (!suggested.contains(p)) {
                    suggested.add(p);
                }
            }
            for (Post p : popularPosts) {
                if (suggested.size() >= limit) {
                    break;
                }
                if (!suggested.contains(p)){
                    suggested.add(p);
                }
            }

            if (suggested.size() < limit) {
                List<Post> allRemaining = new ArrayList<>(allPosts);
                allRemaining.sort((p1, p2) -> p2.getPostDate().compareTo(p1.getPostDate()));
                for (Post p : allRemaining) {
                    if (suggested.size() >= limit){
                        break;
                    }
                    if (!suggested.contains(p) && !p.isDeleted() && !p.isLocked()){
                        suggested.add(p);
                    }
                }
            }
            callback.onResult(suggested, null);
        });
    }

    public List<Post> searchPostsByText(String text) {

        NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.SEARCH_POSTS, text));

        if (response != null && response.getRequestType() == RequestType.SEARCH_POSTS_RESPONSE) {
            return (List<Post>) response.getData();
        }

        return new ArrayList<>();
    }
}