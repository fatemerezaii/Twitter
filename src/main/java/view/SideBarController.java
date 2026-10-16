package view;

import controller.manager.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class SideBarController {

    @FXML private Button homeBtn;
    @FXML private Button searchBtn;
    @FXML private Button profileBtn;
    @FXML private Button newBtn;
    @FXML private Button messagesBtn;
    @FXML private Button logoutBtn;

    @FXML
    public void goToHome(ActionEvent event) throws IOException {
        SceneManager.switchScene(event, "TimeLine.fxml");
    }

    @FXML
    public void goToSearch(ActionEvent event) throws IOException {
        SceneManager.switchScene(event, "Search.fxml");
    }

    @FXML
    public void goToProfile(ActionEvent event) throws IOException {
        SceneManager.switchScene(event, "AdvancedProfile.fxml");
    }

    @FXML
    public void goToNew(ActionEvent event) throws IOException {
        SceneManager.switchScene(event, "Posting.fxml");
    }

    @FXML
    public void goToMessages(ActionEvent event) throws IOException {
        SceneManager.switchScene(event, "messages.fxml");
    }

    @FXML
    public void logout(ActionEvent event) throws IOException {
        SceneManager.switchScene(event, "Gateway.fxml");
    }
}