package view;

import controller.ProfileController;
import controller.ReportController;
import controller.UserConnectionController;
import controller.manager.SessionManager;
import exceptions.NetworkException;
import exceptions.PostNotFoundException;
import exceptions.ReportNotFoundException;
import exceptions.UserNotFoundException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import model.Post;
import model.accounts.users.User;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PublicViewController implements Initializable {

    @FXML
    private AnchorPane aPane;

    @FXML
    private Label bio;

    @FXML
    private Circle blueCircle;

    @FXML
    private Label date;

    @FXML
    private Button followBtn;

    @FXML
    private Label followers;

    @FXML
    private Label followersCount;

    @FXML
    private Label following;

    @FXML
    private Label followingCount;

    @FXML
    private Label fullName;

    @FXML
    private Circle goldCircle;

    @FXML
    private Button reportBtn;

    @FXML
    private Label user;

    @FXML
    private Label year;

    @FXML
    private ListView<Post> postsListView;

    @FXML
    private ComboBox<String> comboBox;

    private static int viewedUserId = -1;

    private User displayedUser;

    private final ProfileController profileController = new ProfileController();
    private final UserConnectionController connectionController = new UserConnectionController();
    private final ReportController reportController = new ReportController();

    public static void setViewedUserId(int userId) {
        viewedUserId = userId;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        blueCircle.setVisible(false);
        goldCircle.setVisible(false);
        loadSidebar();
        setupSortComboBox();
        if (viewedUserId != -1) {
            loadUser();
        }
    }

    private void loadSidebar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SideBar.fxml"));
            AnchorPane pane = loader.load();
            aPane.getChildren().add(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUser() {
        try {
            displayedUser = profileController.getProfile(viewedUserId);
            updateUserData();
            setupFollowButton();
            loadUserPosts();
        } catch (UserNotFoundException | NetworkException e) {
            e.printStackTrace();
            return;
        }
    }

    private void updateUserData() {
        fullName.setText(displayedUser.getFullName());
        user.setText("@" + displayedUser.getUsername());
        bio.setText(displayedUser.getBiography());
        followersCount.setText(String.valueOf(displayedUser.getFollowers().size()));

        followingCount.setText(
                String.valueOf(displayedUser.getFollowings().size())
        );

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(displayedUser.getSignUpDate());

        year.setText(String.valueOf(calendar.get(Calendar.YEAR)));

        blueCircle.setVisible(false);
        goldCircle.setVisible(false);

        if ("Blue".equals(displayedUser.getAccSign())) {
            blueCircle.setVisible(true);
        }

        if ("Gold".equals(displayedUser.getAccSign())) {
            goldCircle.setVisible(true);
        }
    }

    private void setupFollowButton() {

        User current = SessionManager.getCurrentUser();

        if (current == null) {
            followBtn.setDisable(true);
            return;
        }

        if (current.getAccID() == displayedUser.getAccID()) {
            followBtn.setVisible(false);
            return;
        }

        if (current.getFollowings().contains(displayedUser.getAccID())) {
            followBtn.setText("Unfollow");
        } else {
            followBtn.setText("Follow");
        }
    }

    @FXML
    private void follow(ActionEvent event) {

        User current = SessionManager.getCurrentUser();

        if (current == null || displayedUser == null) {
            return;
        }

        boolean success;

        if (followBtn.getText().equals("Follow")) {

            success = connectionController.follow(
                    current.getAccID(),
                    displayedUser.getAccID()
            );

            if (success) {
                followBtn.setText("Unfollow");
                loadUser();
            } else {
                showAlert("Error", "Follow failed.");
            }

        } else {

            success = connectionController.unfollow(
                    current.getAccID(),
                    displayedUser.getAccID()
            );

            if (success) {
                followBtn.setText("Follow");
                loadUser();
            } else {
                showAlert("Error", "Unfollow failed.");
            }
        }
    }
    @FXML
    private void report(ActionEvent event) {

        User current = SessionManager.getCurrentUser();

        if (current == null || displayedUser == null) {
            return;
        }

        TextInputDialog dialog = new TextInputDialog();

        dialog.setTitle("Report User");
        dialog.setHeaderText("Report @" + displayedUser.getUsername());
        dialog.setContentText("Reason:");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent() && !result.get().trim().isEmpty()) {

            try {
                String response =
                        reportController.createReport(
                                current.getAccID(),
                                0,
                                displayedUser.getAccID(),
                                result.get().trim()
                        );

                showAlert("Report", response);

            } catch (ReportNotFoundException | NetworkException e) {

                showAlert("Error", e.getMessage());

            }

        }

    }

    private void loadUserPosts() {

        if (displayedUser == null)
            return;

        try {

            List<Post> posts =
                    profileController.getUserPosts(
                            displayedUser.getAccID()
                    );

            sortAndDisplayPosts(posts);

        } catch (PostNotFoundException | NetworkException e) {

            e.printStackTrace();
            postsListView.getItems().clear();
            showAlert("Error", e.getMessage());

        }
    }

    private void sortAndDisplayPosts(List<Post> posts) {

        if (posts == null) {
            return;
        }

        String sort = comboBox.getValue();

        if ("most liked".equals(sort)) {

            posts.sort((p1, p2) ->
                    Integer.compare(p2.getLikes(), p1.getLikes()));

        } else {

            posts.sort((p1, p2) ->
                    p2.getPostDate().compareTo(p1.getPostDate()));
        }

        postsListView.getItems().setAll(posts);

        postsListView.setCellFactory(list -> new ListCell<Post>() {

            @Override
            protected void updateItem(Post post, boolean empty) {

                super.updateItem(post, empty);

                if (empty || post == null) {
                    setGraphic(null);
                    return;
                }

                try {

                    FXMLLoader loader =
                            new FXMLLoader(
                                    getClass().getResource("/fxml/PostItem.fxml")
                            );

                    setGraphic(loader.load());

                    PostItemController controller =
                            loader.getController();

                    controller.setPost(post);

                } catch (IOException e) {
                    setText("Error");
                }
            }
        });
    }

    private void setupSortComboBox() {

        comboBox.getItems().addAll(
                "new",
                "most liked"
        );

        comboBox.setValue("new");

        comboBox.setOnAction(e -> loadUserPosts());
    }

    private void showAlert(String title, String message) {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);

        alert.setHeaderText(null);

        alert.setContentText(message);

        alert.showAndWait();
    }
}