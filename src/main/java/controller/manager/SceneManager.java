package controller.manager;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {

    private SceneManager() {
    }

    public static void switchScene(ActionEvent event, String fxmlFile) throws IOException {
        load(event, fxmlFile);
    }

    public static void switchScene(MouseEvent event, String fxmlFile) throws IOException {
        load(event, fxmlFile);
    }

    private static void load(Object event, String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/fxml/" + fxmlFile));
        Parent root = loader.load();
        Stage stage;

        if (event instanceof ActionEvent) {
            ActionEvent actionEvent = (ActionEvent) event;
            Node source = (Node) actionEvent.getSource();
            stage = (Stage) source.getScene().getWindow();
        } else {
            MouseEvent mouseEvent = (MouseEvent) event;
            Node source = (Node) mouseEvent.getSource();
            stage = (Stage) source.getScene().getWindow();
        }

        stage.setScene(new Scene(root));
        stage.show();
    }
}