package view;

import controller.manager.SceneManager;
import model.Hashtag;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import network.NetworkManager;
import network.shared.NetworkPacket;
import network.shared.RequestType;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PopularHashtagsController implements Initializable {

    @FXML private ListView<Hashtag> hashtagsListView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupCellFactory();
        loadPopularHashtagsAsync();
    }

    private void loadPopularHashtagsAsync() {
        new Thread(() -> {
            NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.GET_POPULAR_HASHTAGS, null));
            Platform.runLater(() -> {
                if (response != null && response.getRequestType() == RequestType.GET_POPULAR_HASHTAGS_RESPONSE) {
                    List<Hashtag> hashtags = (List<Hashtag>) response.getData();
                    hashtagsListView.getItems().setAll(hashtags);
                }
            });
        }).start();
    }

    private void setupCellFactory() {
        hashtagsListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Hashtag h, boolean empty) {
                super.updateItem(h, empty);
                if (empty || h == null) {
                    setGraphic(null);
                } else {
                    Label label = new Label("#" + h.getTitle() + " (" + h.getHashtagPosts().size() + " posts)");
                    label.setStyle("-fx-text-fill: #1DA1F2;");
                    setGraphic(label);
                }
            }
        });
    }

    @FXML
    public void back(ActionEvent event) throws IOException {
        SceneManager.switchScene(event, "AdminDashboard.fxml");
    }
}