package controller;

import model.Hashtag;
import network.NetworkManager;
import network.shared.NetworkPacket;
import network.shared.RequestType;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

public class HashtagAlgorithmsController {

    public interface SelectionCallback {
        void onResult(String result);
    }

    public void handleHashtagSelectionAsync(int hashtagId, SelectionCallback callback) {

        new Thread(() -> {
            NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.SELECT_HASHTAG, hashtagId));
            Platform.runLater(() -> {
                if (response != null && response.getRequestType() == RequestType.SELECT_HASHTAG_RESPONSE) {
                    callback.onResult((String) response.getData());
                } else {
                    callback.onResult("error");
                }
            });
        }).start();
    }

    public List<Hashtag> searchHashtagsByTitle(String title) {

        NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.SEARCH_HASHTAGS, title));
        if (response != null && response.getRequestType() == RequestType.SEARCH_HASHTAGS_RESPONSE) {
            return (List<Hashtag>) response.getData();
        }
        return new ArrayList<>();
    }
}