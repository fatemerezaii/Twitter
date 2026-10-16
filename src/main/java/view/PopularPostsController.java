package view;

import controller.manager.SceneManager;
import model.Post;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import network.NetworkManager;
import network.shared.NetworkPacket;
import network.shared.RequestType;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PopularPostsController implements Initializable {

    @FXML private ListView<Post> postsListView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupCellFactory();
        loadPopularPostsAsync();
    }

    private void loadPopularPostsAsync() {
        new Thread(() -> {
            NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.GET_POPULAR_POSTS, null));
            Platform.runLater(() -> {
                if (response != null && response.getRequestType() == RequestType.GET_POPULAR_POSTS_RESPONSE) {
                    List<Post> posts = (List<Post>) response.getData();
                    postsListView.getItems().setAll(posts);
                }
            });
        }).start();
    }

    private void setupCellFactory() {
        postsListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Post post, boolean empty) {
                super.updateItem(post, empty);
                if (empty || post == null || post.isDeleted()) {
                    setGraphic(null);
                } else {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PostItem.fxml"));
                        HBox box = loader.load();
                        PostItemController controller = loader.getController();
                        controller.setPost(post);
                        setGraphic(box);
                    } catch (IOException e) {
                        setText("Error loading post");
                    }
                }
            }
        });
    }

    @FXML
    public void back(ActionEvent event) throws IOException {
        SceneManager.switchScene(event, "AdminDashboard.fxml");
    }
}