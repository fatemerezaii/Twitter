package view;

import controller.UsersDataController;
import controller.HashtagAlgorithmsController;
import controller.manager.SceneManager;
import controller.manager.SessionManager;
import controller.PostAlgorithmsController;
import controller.UserConnectionController;
import model.accounts.users.User;
import model.Hashtag;
import model.Post;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SearchController implements Initializable {

    @FXML private AnchorPane aPane;
    @FXML private ListView<User> usernameListView;
    @FXML private ListView<Post> postListView;
    @FXML private ListView<Hashtag> hashtagListView;
    @FXML private TextField searchTextField;
    @FXML private Button sendButton;

    private UsersDataController usersDataController = new UsersDataController();
    private PostAlgorithmsController postAlgoController = new PostAlgorithmsController();
    private HashtagAlgorithmsController hashtagController = new HashtagAlgorithmsController();
    private UserConnectionController connectionController = new UserConnectionController();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SideBar.fxml"));
            AnchorPane sideBarRoot = loader.load();
            aPane.getChildren().add(sideBarRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setupUserListView();
        setupPostListView();
        setupHashtagListView();

        sendButton.setOnAction(e -> performSearch());
    }

    private void performSearch() {
        String keyword = searchTextField.getText().trim();
        if (keyword.isEmpty()) return;

        TabPane tabPane = (TabPane) usernameListView.getParent().getParent().getParent();
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        String tabText = selectedTab.getText();

        switch (tabText) {
            case "Username":
                List<User> users = usersDataController.searchUsers(keyword);
                usernameListView.getItems().setAll(users);
                break;
            case "Post":
                List<Post> posts = postAlgoController.searchPostsByText(keyword);
                postListView.getItems().setAll(posts);
                break;
            case "Hashtag":
                List<Hashtag> hashtags = hashtagController.searchHashtagsByTitle(keyword);
                hashtagListView.getItems().setAll(hashtags);
                break;
        }
    }

    private void setupUserListView() {
        usernameListView.setCellFactory(lv -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setGraphic(null);
                    return;
                }
                VBox vbox = new VBox(5);
                vbox.setPadding(new Insets(5));
                Label nameLabel = new Label(user.getFullName() + "  @" + user.getUsername());
                nameLabel.setStyle("-fx-cursor: hand; -fx-text-fill: #c084fc;");
                nameLabel.setOnMouseClicked((MouseEvent event) -> {
                    PublicViewController.setViewedUserId(user.getAccID());
                    try {
                        SceneManager.switchScene(event, "PublicView.fxml");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                Button followBtn = new Button();
                updateFollowButton(followBtn, user);
                followBtn.setOnAction(e -> {
                    User current = SessionManager.getCurrentUser();
                    if (current == null) return;
                    String result;
                    if (followBtn.getText().equals("Follow")) {
                        result = String.valueOf(connectionController.follow(current.getAccID(), user.getAccID()));
                        if (result.equals("succeed")) {
                            updateFollowButton(followBtn, user);
                        } else {
                            showAlert("Follow Failed", result);
                        }
                    } else {
                        result = String.valueOf(connectionController.unfollow(current.getAccID(), user.getAccID()));
                        if (result.equals("succeed")) {
                            updateFollowButton(followBtn, user);
                        } else {
                            showAlert("Unfollow Failed", result);
                        }
                    }
                });

                vbox.getChildren().addAll(nameLabel, followBtn);
                setGraphic(vbox);
            }

            private void updateFollowButton(Button btn, User target) {
                User current = SessionManager.getCurrentUser();
                if (current != null && current.getFollowings().contains(target.getAccID())) {
                    btn.setText("Unfollow");
                } else {
                    btn.setText("Follow");
                }
            }
        });
    }

    private void setupPostListView() {
        postListView.setCellFactory(lv -> new ListCell<Post>() {
            @Override
            protected void updateItem(Post post, boolean empty) {
                super.updateItem(post, empty);
                if (empty || post == null) {
                    setGraphic(null);
                    return;
                }
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PostItem.fxml"));
                    HBox postBox = loader.load();
                    PostItemController controller = loader.getController();
                    controller.setPost(post);
                    setGraphic(postBox);
                } catch (IOException e) {
                    e.printStackTrace();
                    setText("Error loading post");
                }
            }
        });
    }

    private void setupHashtagListView() {
        hashtagListView.setCellFactory(lv -> new ListCell<Hashtag>() {
            @Override
            protected void updateItem(Hashtag hashtag, boolean empty) {
                super.updateItem(hashtag, empty);
                if (empty || hashtag == null) {
                    setGraphic(null);
                    return;
                }
                Label label = new Label("#" + hashtag.getTitle());
                label.setStyle("-fx-font-size: 14px; -fx-text-fill: #1DA1F2; -fx-cursor: hand;");
                label.setOnMouseClicked(e -> {
                    searchTextField.setText("#" + hashtag.getTitle());
                    performSearch();
                });
                setGraphic(label);
            }
        });
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}