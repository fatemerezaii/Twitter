package view;

import controller.UsersDataController;
import controller.manager.SceneManager;
import controller.manager.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.accounts.users.User;

import java.io.IOException;
import java.util.List;

public class HashtagSelectorController {

    @FXML
    private Button hashtag1, hashtag2, hashtag3, hashtag4, hashtag5, hashtag6;

    @FXML
    private Label errorText;

    @FXML
    public void clickHashtag(ActionEvent event) {
        UsersDataController usersDataController = new UsersDataController();
        Button btn = (Button) event.getSource();
        int id = Integer.parseInt(btn.getId().replace("hashtag", ""));
        User currentUser = SessionManager.getCurrentUser();
        List<Integer> selected = currentUser.getFavoriteHashtags();
        if (selected.contains(id)) {
            usersDataController.removeFavoriteHashtag(id);
            btn.setStyle("");
            errorText.setVisible(false);
            return;
        }

        if (selected.size() >= 4) {
            errorText.setText("Limit reached");
            errorText.setVisible(true);
            return;
        }

        currentUser.addFavoriteHashtag(id);
        btn.setStyle("-fx-background-color: #87CEFA;");
        errorText.setVisible(false);
    }

    @FXML
    public void confirm(ActionEvent event) throws IOException {
        SceneManager.switchScene(event, "TimeLine.fxml");
    }

}