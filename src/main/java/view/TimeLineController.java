package view;

import controller.manager.SessionManager;
import controller.PostAlgorithmsController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import model.accounts.users.User;
import model.Post;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TimeLineController implements Initializable {

    @FXML
    private AnchorPane aPane;
    @FXML
    private ListView<Post> listView;

    private static TimeLineController instance;
    private final PostAlgorithmsController algoController = new PostAlgorithmsController();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SideBar.fxml"));
            AnchorPane sideBarRoot = loader.load();
            aPane.getChildren().add(sideBarRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setupListView();
        loadTimeline();
    }

    private void setupListView() {
        listView.setCellFactory(lv -> new ListCell<Post>() {
            @Override
            protected void updateItem(Post post, boolean empty) {
                super.updateItem(post, empty);
                if (empty || post == null) {
                    setGraphic(null);
                    return;
                }
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PostItem.fxml"));
                    HBox box = loader.load();
                    PostItemController controller = loader.getController();
                    controller.setPost(post);
                    setGraphic(box);
                } catch (Exception e) {
                    e.printStackTrace();
                    setText("Error loading post");
                }
            }
        });
    }

    private void loadTimeline() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) return;
        algoController.getSuggestedPostsForUserAsync(currentUser, 10, (posts, error) -> {
            Platform.runLater(() -> {
                if (error == null && posts != null) {
                    listView.getItems().setAll(posts);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Loading Timeline");
                    alert.setContentText(error != null ? error : "Unknown network error.");
                    alert.showAndWait();
                }
            });
        });
    }
}