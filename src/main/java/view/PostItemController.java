package view;

import controller.manager.SceneManager;
import controller.manager.SessionManager;
import controller.PostingController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.accounts.users.User;
import model.files.File;
import model.files.Image;
import model.files.Video;
import model.Post;
import network.NetworkManager;
import network.shared.NetworkPacket;
import network.shared.RequestType;

import java.io.IOException;

public class PostItemController {

    @FXML private ImageView profileImageView;
    @FXML private Label fullNameLabel;
    @FXML private Label usernameLabel;
    @FXML private Label dateLabel;
    @FXML private Label repliedLabel;
    @FXML private Label postTextLabel;
    @FXML private Button likeButton;
    @FXML private Button replyButton;
    @FXML private Button shareButton;
    @FXML private ImageView mediaThumbnail;
    @FXML private Circle badgeCircle;

    private Post post;
    private final PostingController postingController = new PostingController();

    public void setPost(Post post) {
        this.post = post;
        displayPostDataAsync();
        updateLikeButtonStyle();
        displayMediaThumbnail();
        User current = SessionManager.getCurrentUser();
        if (current != null && post.getPosterID() == current.getAccID()) {
            likeButton.setDisable(true);
            likeButton.setText("♡ " + post.getLikes() + " (your post)");
        }

        if (post.getParentPostID() != 0) {
            String parentUsername = "unknown";
            repliedLabel.setText("Replied to @" + parentUsername);
            repliedLabel.setVisible(true);
        } else {
            repliedLabel.setVisible(false);
        }
    }

    private void displayPostDataAsync() {
        postTextLabel.setText(post.getText());
        dateLabel.setText(post.getPostDate().toString());
        likeButton.setText("♡ " + post.getLikes());
        new Thread(() -> {
            NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.GET_PROFILE, post.getPosterID()));
            Platform.runLater(() -> {
                if (response != null && response.getRequestType() == RequestType.PROFILE_RESPONSE) {
                    User author = (User) response.getData();
                    fullNameLabel.setText(author.getFullName());
                    usernameLabel.setText("@" + author.getUsername());
                    setBadge(author);
                } else {
                    fullNameLabel.setText("Unknown");
                    usernameLabel.setText("@unknown");
                    badgeCircle.setVisible(false);
                }
            });
        }).start();
    }

    private void setBadge(User author) {
        if (author == null) {
            badgeCircle.setVisible(false);
            return;
        }
        String sign = author.getAccSign();
        if (sign == null || sign.isEmpty()) {
            badgeCircle.setVisible(false);
        } else if (sign.equals("Blue")) {
            badgeCircle.setVisible(true);
            badgeCircle.setFill(Color.DODGERBLUE);
            badgeCircle.setStroke(Color.DODGERBLUE);
            badgeCircle.setStrokeWidth(1);
        } else if (sign.equals("Gold")) {
            badgeCircle.setVisible(true);
            badgeCircle.setFill(Color.GOLD);
            badgeCircle.setStroke(Color.ORANGE);
            badgeCircle.setStrokeWidth(1);
        } else {
            badgeCircle.setVisible(false);
        }
    }

    private void displayMediaThumbnail() {
        File media = post.getMedia();
        if (media == null) {
            mediaThumbnail.setVisible(false);
            return;
        }
        mediaThumbnail.setVisible(true);
        if (media instanceof Image) {
            String path = media.getFilePath();
            try {
                String url = path.startsWith("file:") ? path : "file:" + path;
                javafx.scene.image.Image fxImage = new javafx.scene.image.Image(url, 200, 150, true, true);
                mediaThumbnail.setImage(fxImage);
                mediaThumbnail.setStyle("");
            } catch (Exception e) {
                e.printStackTrace();
                mediaThumbnail.setVisible(false);
            }
        } else if (media instanceof Video) {
            mediaThumbnail.setImage(null);
            mediaThumbnail.setStyle("-fx-background-color: #333; -fx-alignment: center;");
        }
    }

    private void updateLikeButtonStyle() {
        User current = SessionManager.getCurrentUser();
        if (current != null && post.getLikers().contains(current.getAccID())) {
            likeButton.setStyle("-fx-text-fill: red;");
        } else {
            likeButton.setStyle("");
        }
    }

    @FXML
    private void onLikeClick() {
        User current = SessionManager.getCurrentUser();
        if (current == null || post.getPosterID() == current.getAccID()) return;

        if (!post.getLikers().contains(current.getAccID())) {
            postingController.likePostAsync(post.getPostID(), current.getAccID(), success -> {
                Platform.runLater(() -> {
                    if (success) {
                        post.getLikers().add(current.getAccID());
                        post.setLikes(post.getLikes() + 1);
                        updateLikeButtonStyle();
                        likeButton.setText("♡ " + post.getLikes());
                    }
                });
            });
        }
    }

    @FXML
    private void onReplyClick(ActionEvent event) {
        try {
            PostViewerController.setCurrentPostId(post.getPostID());
            SceneManager.switchScene(event, "PostDetail.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}