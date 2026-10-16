package network.server.handler;

import model.Post;
import network.shared.NetworkPacket;
import network.shared.ReplyRequest;
import network.shared.RequestType;
import repository.PostRepository;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class PostHandler {

    private final PostRepository repository = new PostRepository();

    public PostHandler() {
    }

    public void handle(NetworkPacket packet, ObjectOutputStream output) throws IOException {
        switch (packet.getRequestType()) {
            case GET_POPULAR_POSTS:
                List<Post> popularPosts = repository.getPopularPosts();
                output.writeObject(new NetworkPacket(RequestType.GET_POPULAR_POSTS_RESPONSE, popularPosts));
                output.flush();
                break;

            case CREATE_POST:
                createPost(packet, output);
                break;

            case DELETE_POST:
                deletePost(packet, output);
                break;

            case EDIT_POST:
                editPost(packet, output);
                break;


            case LIKE_POST:
                likePost(packet, output);
                break;

            case REPLY_POST:
                replyPost(packet, output);
                break;

            case REPLY_TO_POST:
                replyToPost(packet, output);
                break;


            case GET_REPLIES:
                getReplies(packet, output);
                break;

            case GET_POSTS:
                List<Post> posts;

                if (packet.getData() == null) {
                    posts = repository.findAll();
                } else {
                    int userId = (Integer) packet.getData();
                    posts = repository.getPostsByUser(userId);
                }

                output.writeObject(new NetworkPacket(RequestType.GET_POSTS_RESPONSE, posts));
                output.flush();
                break;

            case UNLIKE_POST:
                unlikePost(packet, output);
                break;

            case VIEW_POST:
                viewPost(packet, output);
                break;

            case SEARCH_POSTS:
                searchPosts(packet, output);
                break;

            default:
                break;
        }

    }

    private void createPost(NetworkPacket packet, ObjectOutputStream output) {
        Post post = (Post) packet.getData();
        boolean success = repository.add(post);
        sendBoolean(output, RequestType.CREATE_POST_RESPONSE, success);
    }

    private void deletePost(NetworkPacket packet, ObjectOutputStream output) {
        int id = (Integer) packet.getData();
        boolean success = repository.delete(id);
        sendBoolean(output, RequestType.DELETE_POST_RESPONSE, success);
    }

    private void editPost(NetworkPacket packet, ObjectOutputStream output) {
        Post post = (Post) packet.getData();
        boolean success = repository.update(post);
        sendBoolean(output, RequestType.EDIT_POST_RESPONSE, success);
    }

    private void likePost(NetworkPacket packet, ObjectOutputStream output) {
        int[] ids = (int[]) packet.getData();
        boolean success = repository.likePost(ids[0], ids[1]);
        sendBoolean(output, RequestType.LIKE_POST_RESPONSE, success);
    }

    private void replyPost(NetworkPacket packet, ObjectOutputStream output) {
        Post reply = (Post) packet.getData();
        boolean success = repository.add(reply);
        sendBoolean(output, RequestType.REPLY_POST_RESPONSE, success);
    }

    private void sendBoolean(ObjectOutputStream output, RequestType type, boolean value) {
        try {
            output.writeObject(new NetworkPacket(type, value));
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void replyToPost(NetworkPacket packet, ObjectOutputStream output) {
        ReplyRequest req = (ReplyRequest) packet.getData();
        boolean success = repository.addReply(req.parentId, req.text);
        sendBoolean(output, RequestType.REPLY_SUCCESS, success);
    }

    void getReplies(NetworkPacket packet, ObjectOutputStream output) {
        int postId = (Integer) packet.getData();
        List<Post> replies = repository.findReplies(postId);
        try {
            output.writeObject(new NetworkPacket(RequestType.GET_REPLIES_RESPONSE, replies));
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void viewPost(NetworkPacket packet, ObjectOutputStream output) {
        int postId = (Integer) packet.getData();
        Post post = repository.findById(postId);
        try {
            output.writeObject(new NetworkPacket(RequestType.VIEW_POST_RESPONSE, post));
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void unlikePost(NetworkPacket packet, ObjectOutputStream output) {
        int[] ids = (int[]) packet.getData();
        boolean success = repository.unlikePost(ids[0], ids[1]);
        sendBoolean(output, RequestType.UNLIKE_POST_RESPONSE, success);
    }

    private void searchPosts(NetworkPacket packet, ObjectOutputStream output) {
        String keyword = (String) packet.getData();
        List<Post> posts = repository.search(keyword);
        try {
            output.writeObject(new NetworkPacket(RequestType.SEARCH_POSTS_RESPONSE, posts));
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}