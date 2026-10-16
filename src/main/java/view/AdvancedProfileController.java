package view;

import controller.manager.SessionManager;
import controller.ProfileController;
import controller.SubscriptionsController;
import controller.UserInfoController;
import exceptions.InsufficientCreditException;
import exceptions.InvalidSubscriptionException;
import exceptions.UserNotFoundException;
import javafx.scene.input.MouseEvent;
import model.accounts.users.User;
import model.Post;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class AdvancedProfileController implements Initializable {
    @FXML private AnchorPane aPane;
    @FXML private Label bio, fullName, username, year, followersCount, followingCount, errorText, errorText1;
    @FXML private Circle blueCircle, goldCircle;
    @FXML private ListView<Post> postListView, mediaListView, replyListView;
    @FXML private ComboBox<String> comboBox;
    @FXML private TextField name, email, biography, chargeCredit, chargeTokenId;
    @FXML private PasswordField pass;

    private ProfileController profileController = new ProfileController();
    private UserInfoController userInfoController = new UserInfoController();
    private final controller.UsersDataController usersDataController = new controller.UsersDataController();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadSidebar();
        setupPostListView();
        setupReplyListView();
        setupMediaListView();
        loadAllUserContent();
        setupSortComboBox();
    }

    private void setupMediaListView() {
        mediaListView.setCellFactory(postListView.getCellFactory());
    }

    private void setupReplyListView() {
        replyListView.setCellFactory(postListView.getCellFactory());
    }

    private void setupPostListView() {
        postListView.setCellFactory(lv -> new ListCell<Post>() {

            @Override
            protected void updateItem(Post post, boolean empty) {
                super.updateItem(post, empty);
                if (empty || post == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PostItem.fxml"));
                    HBox root = loader.load();
                    PostItemController controller = loader.getController();
                    controller.setPost(post);
                    setGraphic(root);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadAllUserContent() {
        int userId = SessionManager.getCurrentUser().getAccID();
        profileController.getProfileAsync(userId, user -> Platform.runLater(() -> updateUI(user)));
        profileController.getUserPostsAsync(userId, posts -> Platform.runLater(() -> postListView.getItems().setAll(posts)));
        profileController.getUserRepliesAsync(userId, replies -> Platform.runLater(() -> replyListView.getItems().setAll(replies)));
        profileController.getUserMediaAsync(userId, media -> Platform.runLater(() -> mediaListView.getItems().setAll(media)));
    }

    private void updateUI(User user) {
        if (user == null) return;
        fullName.setText(user.getFullName());
        username.setText("@" + user.getUsername());
        bio.setText(user.getBiography());
        followersCount.setText(String.valueOf(user.getFollowers().size()));
        followingCount.setText(String.valueOf(user.getFollowings().size()));
        blueCircle.setVisible("Blue".equals(user.getAccSign()));
        goldCircle.setVisible("Gold".equals(user.getAccSign()));
    }

    @FXML
    public void edit(ActionEvent event) {
        String error = userInfoController.editing(name.getText(), biography.getText(), email.getText(), pass.getText());
        errorText.setText(error);
        errorText.setVisible(true);
        if ("succeed".equals(error)) loadAllUserContent();
    }

    @FXML
    public void buyBlue(ActionEvent event) {

        try {
            String result = new SubscriptionsController().buySubscription("Blue");
            errorText1.setText(result);
            if ("Succeed".equals(result)) {
                loadAllUserContent();
            }
        } catch (UserNotFoundException | InsufficientCreditException | InvalidSubscriptionException e) {
            errorText1.setText(e.getMessage());
        }
    }

    @FXML
    public void charge(ActionEvent event) {
        try {
            int amount = Integer.parseInt(chargeCredit.getText());
            new SubscriptionsController().chargeCredit(amount);
            chargeCredit.clear();
            errorText1.setText("Credit charged successfully.");
        } catch (NumberFormatException e) {
            errorText1.setText("Invalid amount");
        } catch (UserNotFoundException e) {
            errorText1.setText(e.getMessage());
        }
    }

    private void loadSidebar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SideBar.fxml"));
            aPane.getChildren().add(loader.load());
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void setupSortComboBox() {
        comboBox.getItems().addAll("new", "most liked");
        comboBox.setValue("new");
    }

    @FXML
    public void showFollowings(MouseEvent event) {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null){
            return;}
        List<String> usernames = new ArrayList<>();
        for (Integer id : currentUser.getFollowings()) {
            User user = usersDataController.getUser(id);
            if (user != null) {
                usernames.add("@" + user.getUsername());
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Followings");
        alert.setHeaderText(null);

        if (usernames.isEmpty()) {
            alert.setContentText("No followings.");
        } else {
            alert.setContentText(String.join("\n", usernames));
        }
        alert.showAndWait();
    }

    @FXML
    public void showFollowers(MouseEvent event) {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            return;
        }
        List<String> usernames = new ArrayList<>();
        for (Integer id : currentUser.getFollowers()) {
            User user = usersDataController.getUser(id);
            if (user != null) {
                usernames.add("@" + user.getUsername());
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Followers");
        alert.setHeaderText(null);
        if (usernames.isEmpty()) {
            alert.setContentText("No followers.");
        } else {
            alert.setContentText(String.join("\n", usernames));
        }
        alert.showAndWait();
    }

    @FXML
    public void chargeToken(ActionEvent event) {
        try {
            int amount = Integer.parseInt(chargeTokenId.getText());
            new SubscriptionsController().chargeToken(amount);
            chargeTokenId.clear();
            errorText1.setText("Tokens charged successfully.");
        } catch (NumberFormatException e) {
            errorText1.setText("Invalid amount");
        } catch (UserNotFoundException e) {
            errorText1.setText(e.getMessage());
        }
    }

    @FXML
    public void buyGold(ActionEvent event) {
        try {
            String result = new SubscriptionsController().buySubscription("Gold");
            errorText1.setText(result);
            if ("Succeed".equals(result)) {
                loadAllUserContent();
            }
        } catch (UserNotFoundException | InvalidSubscriptionException | InsufficientCreditException e) {
            errorText1.setText(e.getMessage());
        }
    }
}