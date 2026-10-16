package view;

import controller.manager.SceneManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import model.Post;
import network.NetworkManager;
import network.shared.NetworkPacket;
import network.shared.RequestType;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AllPostsController implements Initializable {

    @FXML private ListView<Post> postsListView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupCellFactory();
        loadPostsAsync();
    }

    private void loadPostsAsync() {
        new Thread(() -> {
            try {
                NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.GET_POSTS, null));
                Platform.runLater(() -> {
                    if (response != null && response.getRequestType() == RequestType.GET_POSTS_RESPONSE) {
                        List<Post> posts = (List<Post>) response.getData();
                        postsListView.getItems().setAll(posts);
                    } else {
                        showAlert("Error", "Failed to load posts from server.");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> showAlert("Error", "Network error: " + e.getMessage()));
            }
        }).start();
    }

    private void setupCellFactory() {
        postsListView.setCellFactory(lv -> new ListCell<Post>() {
            @Override
            protected void updateItem(Post post, boolean empty) {super.updateItem(post, empty);
                if (empty || post == null) {
                    setGraphic(null);
                    return;
                }
                HBox hbox = new HBox(20);
                hbox.setPadding(new Insets(5));
                String postSnippet = post.getText().length() > 30 ? post.getText().substring(0, 30) + "..." : post.getText();
                Label postInfo = new Label("ID: " + post.getPostID() + " | " + postSnippet);
                Button lockBtn = new Button(post.isLocked() ? "Unlock" : "Lock");
                lockBtn.setStyle(post.isLocked() ? "-fx-background-color: #2ecc71;" : "-fx-background-color: #f39c12;");
                lockBtn.setOnAction(e -> handleLockToggle(post, lockBtn));
                hbox.getChildren().addAll(postInfo, lockBtn);
                setGraphic(hbox);
            }
        });
    }

    private void handleLockToggle(Post post, Button lockBtn) {
        lockBtn.setDisable(true);
        boolean isCurrentlyLocked = post.isLocked();
        boolean newLockState = !isCurrentlyLocked;
        new Thread(() -> {
            try {
                post.setLocked(newLockState);
                NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.EDIT_POST, post));
                Platform.runLater(() -> {
                    lockBtn.setDisable(false);
                    if (response != null && response.getRequestType() == RequestType.EDIT_POST_RESPONSE && (Boolean) response.getData()) {
                        lockBtn.setText(newLockState ? "Unlock" : "Lock");
                        lockBtn.setStyle(newLockState ? "-fx-background-color: #2ecc71;" : "-fx-background-color: #f39c12;");
                        showAlert("Success", "Post " + post.getPostID() + (newLockState ? " locked." : " unlocked."));
                        postsListView.refresh();
                    } else {
                        post.setLocked(isCurrentlyLocked);
                        showAlert("Error", "Failed to update lock status on server.");
                    }
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    post.setLocked(isCurrentlyLocked);
                    lockBtn.setDisable(false);
                    showAlert("Error", "Network error: " + ex.getMessage());
                });
            }
        }).start();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    public void back(ActionEvent event) throws IOException {
        SceneManager.switchScene(event, "AdminDashboard.fxml");
    }
}