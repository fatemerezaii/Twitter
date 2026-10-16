package example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Gateway.fxml"));
        Scene scene = new Scene(loader.load(), 600, 500);
        stage.setTitle("Twitter!");
        stage.setScene(scene);
        stage.show();
    }
}
