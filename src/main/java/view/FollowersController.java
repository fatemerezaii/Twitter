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

public class FollowersController {

    @FXML private ListView<String> listView;
    private ObservableList<String> followersNames = FXCollections.observableArrayList();

    public void loadFollowersAsync(int userId) {
        new Thread(() -> {
            NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.GET_FOLLOWERS, userId));
            Platform.runLater(() -> {
                followersNames.clear();
                if (response != null && response.getRequestType() == RequestType.GET_FOLLOWERS_RESPONSE) {
                    List<String> usernames = (List<String>) response.getData();
                    if (usernames == null || usernames.isEmpty()) {
                        followersNames.add("No followers");
                    } else {
                        followersNames.addAll(usernames);
                    }
                } else {
                    followersNames.add("Failed to load followers");
                }
                listView.setItems(followersNames);
            });
        }).start();
    }
}