package view;

import controller.manager.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class AdminDashboardController {

    @FXML private Button popularHashtags;
    @FXML private Button popularPosts;
    @FXML private Button postsList;
    @FXML private Button usersList;
    @FXML private Button manageReportBtn;
    @FXML private Button logoutBtn;

    public void manageReports(ActionEvent event) throws IOException {
        SceneManager.switchScene(event, "ReportManager.fxml");
    }

    public void popularHashtags(ActionEvent event) throws IOException {
        SceneManager.switchScene(event, "PopularHashtags.fxml");
    }

    public void popularPosts(ActionEvent event) throws IOException {
        SceneManager.switchScene(event, "PopularPosts.fxml");
    }

    public void postsList(ActionEvent event) throws IOException {
        SceneManager.switchScene(event, "AllPosts.fxml");
    }

    public void usersList(ActionEvent event) throws IOException {
        SceneManager.switchScene(event, "AllUsers.fxml");
    }

    public void logout(ActionEvent actionEvent) throws IOException {
        SceneManager.switchScene(actionEvent, "Gateway.fxml");
    }
}