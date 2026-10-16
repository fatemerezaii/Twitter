package view;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import network.NetworkManager;
import network.shared.NetworkPacket;
import network.shared.RequestType;

import java.util.List;

public class FollowingsController {

    @FXML private ListView<String> listView;
    private ObservableList<String> followingsNames = FXCollections.observableArrayList();

    public void loadFollowingsAsync(int userId) {
        new Thread(() -> {
            NetworkPacket response = NetworkManager.getInstance().sendAndReceive(
                    new NetworkPacket(RequestType.GET_FOLLOWINGS, userId)
            );

            Platform.runLater(() -> {
                followingsNames.clear();
                if (response != null && response.getRequestType() == RequestType.GET_FOLLOWINGS_RESPONSE) {
                    List<String> usernames = (List<String>) response.getData();
                    if (usernames == null || usernames.isEmpty()) {
                        followingsNames.add("No followings");
                    } else {
                        followingsNames.addAll(usernames);
                    }
                } else {
                    followingsNames.add("Failed to load followings");
                }
                listView.setItems(followingsNames);
            });
        }).start();
    }
}