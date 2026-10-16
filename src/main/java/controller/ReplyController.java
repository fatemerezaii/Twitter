package controller;

import exceptions.NetworkException;
import exceptions.PostNotFoundException;
import network.NetworkManager;
import network.shared.NetworkPacket;
import network.shared.ReplyRequest;
import network.shared.RequestType;
import model.Post;

import java.util.List;

public class ReplyController {

    public boolean replyToPost(int parentPostId, String replyText) throws NetworkException, PostNotFoundException {

        NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.REPLY_TO_POST, new ReplyRequest(parentPostId, replyText)));

        if (response == null) {
            throw new NetworkException("No response from server.");
        }

        if (response.getRequestType() != RequestType.REPLY_SUCCESS) {
            throw new PostNotFoundException("Reply could not be added.");
        }

        return (Boolean) response.getData();
    }

    public List<Post> getRepliesList(int postId) throws NetworkException {
        NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.GET_REPLIES, postId));

        if (response == null) {
            throw new NetworkException("No response from server.");
        }

        if (response.getRequestType() != RequestType.GET_REPLIES_RESPONSE) {
            throw new NetworkException("Failed to load replies.");
        }

        return (List<Post>) response.getData();
    }
}