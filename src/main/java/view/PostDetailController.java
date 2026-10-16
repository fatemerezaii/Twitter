package view;

import controller.ReportController;
import controller.manager.SceneManager;
import controller.manager.SessionManager;
import controller.PostingController;
import controller.ReplyController;
import exceptions.NetworkException;
import exceptions.PostNotFoundException;
import exceptions.ReportNotFoundException;
import exceptions.UserNotFoundException;
import model.accounts.users.User;
import model.files.Image;
import model.files.Video;
import model.Post;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import network.NetworkManager;
import network.shared.NetworkPacket;
import network.shared.RequestType;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PostDetailController implements Initializable {

    @FXML
    private AnchorPane sideBarPane;
    @FXML
    private VBox postContainer;
    @FXML
    private Label fullNameLabel, usernameLabel, dateLabel, postTextLabel, likesLabel, viewsLabel, videoTimeLabel;
    @FXML
    private Button likeButton, replyButton, shareButton, editButton, deleteButton, reportBtn, playPauseBtn;
    @FXML
    private TextArea replyTextArea;
    @FXML
    private ListView<Post> repliesListView;
    @FXML
    private ImageView imgView;
    @FXML
    private MediaView mediaView;
    @FXML
    private Slider volumeSlider;
    @FXML
    private VBox videoContainer;
    @FXML
    private AnchorPane aPane;

    private PostingController postingController = new PostingController();
    private ReplyController replyController = new ReplyController();
    private Post currentPost;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SideBar.fxml"));
            AnchorPane sideBarRoot = loader.load();
            aPane.getChildren().add(sideBarRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int postId = PostViewerController.getCurrentPostId();
        if (postId != 0) loadPost(postId);
        setupRepliesListView();
    }

    private void loadPost(int postId) {
        new Thread(() -> {
            try {
                Post post = postingController.viewPost(postId);
                Platform.runLater(() -> {
                    this.currentPost = post;
                    displayPost(post);
                    loadReplies();
                });
            } catch (PostNotFoundException e) {
                Platform.runLater(() -> {
                    postContainer.setVisible(false);
                    showAlert("Error", e.getMessage());
                });
            }
        }).start();
    }

    private void displayPost(Post post) {
        postTextLabel.setText(post.getText());
        dateLabel.setText(post.getPostDate().toString());
        likesLabel.setText("♡ " + post.getLikes());
        viewsLabel.setText("⊙ " + post.getViews());
        new Thread(() -> {
            NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.GET_PROFILE, post.getPosterID()));
            Platform.runLater(() -> {
                if (response != null && response.getRequestType() == RequestType.PROFILE_RESPONSE) {
                    User user = (User) response.getData();
                    fullNameLabel.setText(user.getFullName());
                    usernameLabel.setText("@" + user.getUsername());
                }
            });
        }).start();
        User current = SessionManager.getCurrentUser();
        if (current != null) {
            likeButton.setText(post.getLikers().contains(current.getAccID()) ? "Unlike" : "Like");
            likeButton.setDisable(post.getPosterID() == current.getAccID());
        }
    }

    @FXML
    private void onLikeClick() {
        if (currentPost == null) {
            return;
        }
        new Thread(() -> {
            try {
                User currentUser = SessionManager.getCurrentUser();
                boolean liked = currentPost.getLikers().contains(currentUser.getAccID());
                boolean success;
                if (liked) {
                    success = postingController.unlikePost(currentPost.getPostID());
                } else {
                    success = postingController.likePost(currentPost.getPostID());
                }
                if (success) {
                    loadPost(currentPost.getPostID());
                }
            } catch (UserNotFoundException e) {
                Platform.runLater(() -> showAlert("Error", e.getMessage()));
            }
        }).start();
    }

    private void loadReplies() {
        new Thread(() -> {
            try {
                List<Post> replies = replyController.getRepliesList(currentPost.getPostID());
                Platform.runLater(() -> repliesListView.getItems().setAll(replies));
            } catch (NetworkException e) {
                Platform.runLater(() -> showAlert("Error", e.getMessage()));
            }
        }).start();
    }

    @FXML
    private void postReply() {
        String text = replyTextArea.getText();
        if (text == null || text.trim().isEmpty()) {
            return;
        }
        new Thread(() -> {
            try {
                if (replyController.replyToPost(currentPost.getPostID(), text)) {
                    Platform.runLater(() -> {
                        replyTextArea.clear();
                        loadReplies();
                    });
                }
            } catch (PostNotFoundException | NetworkException e) {
                Platform.runLater(() -> showAlert("Error", e.getMessage()));
            }
        }).start();
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void setupRepliesListView() {
        repliesListView.setCellFactory(lv -> new ListCell<Post>() {
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
                    setText("Error loading reply");
                }
            }
        });
    }

    public void loadPostById(int postId) {
        loadPost(postId);
    }

    @FXML
    public void togglePlayPause(ActionEvent event) {
        if (mediaPlayer == null) {
            return;
        }
        if (isPlaying) {
            mediaPlayer.pause();
            playPauseBtn.setText("Play");
        } else {
            mediaPlayer.play();
            playPauseBtn.setText("Pause");
        }
        isPlaying = !isPlaying;
    }

    @FXML
    public void onReplyAction(ActionEvent event) {
        postReply();
    }

    @FXML
    public void onShareClick(ActionEvent event) {
        if (currentPost == null) {
            return;
        }
        String link = "tweetapp://post/" + currentPost.getPostID();
        javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
        content.putString(link);
        clipboard.setContent(content);
        showAlert("Share", "Post link copied to clipboard:\n\n" + link);
    }

    @FXML
    public void report(ActionEvent event) {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null || currentPost == null) {
            return;
        }
        try {
            String result = new ReportController().createReport(currentUser.getAccID(), currentPost.getPostID(), 0, "Reported from post page");
            showAlert("Report", result);
        } catch (ReportNotFoundException | NetworkException e) {
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    public void edit(ActionEvent event) {
        if (currentPost == null) {
            return;
        }
        TextInputDialog dialog = new TextInputDialog(currentPost.getText());
        dialog.setTitle("Edit Post");
        dialog.setHeaderText(null);
        dialog.setContentText("New text:");
        dialog.showAndWait().ifPresent(text -> {
            currentPost.setText(text);
            try {
                postingController.editPostAsync(currentPost, success -> {
                    if (success) {
                        Platform.runLater(() -> {
                            showAlert("Success", "Post edited.");
                            loadPost(currentPost.getPostID());
                        });
                    } else {
                        Platform.runLater(() -> showAlert("Error", "Failed to edit post."));
                    }
                });
            } catch (PostNotFoundException e) {
                showAlert("Error", e.getMessage());
            }
        });
    }

    @FXML
    public void delete(ActionEvent event) {
        if (currentPost == null) {
            return;
        }
        try {
            postingController.deletePostAsync(currentPost.getPostID(), success -> Platform.runLater(() -> {
                        if (success) {
                            showAlert("Success", "Post deleted.");
                            Stage stage = (Stage) deleteButton.getScene().getWindow();
                            postContainer.setVisible(false);
                            try {
                                SceneManager.switchScene(event, "/fxml/TimeLine.fxml");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    })
            );
        } catch (PostNotFoundException e) {
            showAlert("Error", e.getMessage());
        }
    }
}