package view;

import controller.ChatController;
import controller.manager.SceneManager;
import controller.manager.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.ChatMessage;
import model.MessageStatus;
import model.accounts.users.User;
import network.shared.NetworkPacket;
import network.shared.RequestType;

import javafx.event.ActionEvent;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class MessageController implements Initializable {

    @FXML
    private ListView<String> usersListView;

    @FXML
    private ListView<String> messagesListView;

    @FXML
    private TextField messageField;

    @FXML
    private Label connectionLabel;

    private final ChatController chatController = new ChatController();
    private User currentUser;
    private User selectedUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = SessionManager.getCurrentUser();
        if (!chatController.isConnected()) {
            if (!chatController.connect("localhost", 8080)) {
                connectionLabel.setText("Offline");
                return;
            }
        }

        connectionLabel.setText("Connected");
        loadUsers();
        usersListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }

            for (User user : chatController.getOnlineUsers()) {
                if (user.getUsername().equals(newValue)) {
                    selectedUser = user;
                    loadHistory();
                    break;
                }
            }
        });

        startPacketListener();
    }

    private void startPacketListener() {
        Thread thread = new Thread(() -> {
            while (chatController.isConnected()) {
                NetworkPacket packet = chatController.listen();
                if (packet == null) {
                    continue;
                }
                Platform.runLater(() -> process(packet));
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void process(NetworkPacket packet) {
        if (packet.getRequestType() != RequestType.RECEIVE_MESSAGE) {
            return;
        }
        ChatMessage message = (ChatMessage) packet.getData();
        if (selectedUser == null) {
            return;
        }

        if (message.getSenderId() == selectedUser.getAccID() || message.getReceiverId() == selectedUser.getAccID()) {
            if (message.getSenderId() == currentUser.getAccID()) {
                messagesListView.getItems().add("You : " + message.getContent());
            } else {
                messagesListView.getItems().add(message.getContent());
            }
        }
    }

    private void loadUsers() {
        usersListView.getItems().clear();
        List<User> users = chatController.getOnlineUsers();
        for (User u : users) {
            if (u.getAccID() != currentUser.getAccID()) {
                usersListView.getItems().add(u.getUsername());
            }

        }

    }

    @FXML
    private void refreshUsers() {
        loadUsers();
    }

    @FXML
    private void sendMessage() {
        if (selectedUser == null){
            return;}
        String text = messageField.getText().trim();
        if (text.isEmpty()){
            return;}
        ChatMessage message = new ChatMessage();
        message.setSenderId(currentUser.getAccID());
        message.setReceiverId(selectedUser.getAccID());
        message.setContent(text);
        message.setSendTime(LocalDateTime.now());
        message.setStatus(MessageStatus.SENT);
        if (chatController.sendMessage(message)) {
            messagesListView.getItems().add("You : " + text);
            messageField.clear();
        } else {
            connectionLabel.setText("Send Failed");
        }
    }

    private void loadHistory() {
        messagesListView.getItems().clear();
        List<ChatMessage> history = chatController.loadHistory(selectedUser.getAccID());
        for (ChatMessage message : history) {
            if (message.getSenderId() == currentUser.getAccID()) {
                messagesListView.getItems().add("You : " + message.getContent());
            } else {
                messagesListView.getItems().add(message.getContent());
            }
        }
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            SceneManager.switchScene(event, "TimeLine.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}