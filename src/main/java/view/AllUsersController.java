package view;

import controller.manager.SceneManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import model.accounts.users.User;
import network.NetworkManager;
import network.shared.NetworkPacket;
import network.shared.RequestType;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AllUsersController implements Initializable {

    @FXML private ListView<User> usersListView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupCellFactory();
        loadUsersAsync();
    }

    private void loadUsersAsync() {
        new Thread(() -> {
            NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.GET_ALL_USERS, null));
            Platform.runLater(() -> {
                if (response != null && response.getRequestType() == RequestType.GET_ALL_USERS_RESPONSE) {
                    List<User> users = (List<User>) response.getData();
                    usersListView.getItems().setAll(users);
                }
            });
        }).start();
    }

    private void setupCellFactory() {
        usersListView.setCellFactory(lv -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setGraphic(null);
                    return;
                }
                HBox hbox = new HBox(20);
                hbox.setPadding(new Insets(5));
                Label nameLabel = new Label(user.getFullName() + "  @" + user.getUsername());
                nameLabel.setStyle("-fx-text-fill: #c084fc; -fx-cursor: hand;");
                nameLabel.setOnMouseClicked(e -> {
                    try { PublicViewController.setViewedUserId(user.getAccID());
                        SceneManager.switchScene(e, "PublicView.fxml"); }
                    catch (IOException ex) { ex.printStackTrace(); }
                });
                Button blockBtn = new Button(user.isBlocked() ? "Unblock" : "Block");
                blockBtn.setStyle(user.isBlocked() ? "-fx-background-color: #2ecc71;" : "-fx-background-color: #e74c3c;");
                blockBtn.setOnAction(e -> handleBlockToggle(user, blockBtn));
                hbox.getChildren().addAll(nameLabel, blockBtn);
                setGraphic(hbox);
            }
        });
    }

    private void handleBlockToggle(User user, Button blockBtn) {
        blockBtn.setDisable(true);
        boolean targetState = !user.isBlocked();
        new Thread(() -> {
            NetworkPacket response = NetworkManager.getInstance().sendAndReceive(new NetworkPacket(RequestType.BLOCK_USER, new Object[]{user.getAccID(), targetState}));
            Platform.runLater(() -> {
                blockBtn.setDisable(false);
                if (response != null && (Boolean) response.getData()) {
                    user.setBlocked(targetState);
                    blockBtn.setText(targetState ? "Unblock" : "Block");
                    blockBtn.setStyle(targetState ? "-fx-background-color: #2ecc71;" : "-fx-background-color: #e74c3c;");
                    showAlert("Success", "User " + user.getUsername() + (targetState ? " has been blocked." : " has been unblocked."));
                } else {
                    showAlert("Error", "Server failed to update block status.");
                }
            });
        }).start();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    public void back(ActionEvent event) throws IOException {
        SceneManager.switchScene(event, "AdminDashboard.fxml");
    }
}