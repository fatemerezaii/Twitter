package view;

import controller.manager.SceneManager;
import controller.EntryController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class GatewayController implements Initializable {

    @FXML
    private PasswordField confirmPassTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private Label errorText;
    @FXML
    private Label errorText1;
    @FXML
    private Label loginLabel;
    @FXML
    private TextField nameTextField;
    @FXML
    private PasswordField passTextField;
    @FXML
    private PasswordField passTextField1;
    @FXML
    private TextField phoneTextField;
    @FXML
    private Label registerLabel;
    @FXML
    private Button signInButton;
    @FXML
    private Button signUpButton;
    @FXML
    private TextField userTextField;
    @FXML
    private TextField userTextField1;
    @FXML
    private DatePicker bday;

    private final EntryController entryController = new EntryController();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clearErrors();
    }

    public void putText(int number, String text) {
        Platform.runLater(() -> {
            if (number == 1) {
                errorText.setText(text);
                errorText.setVisible(true);
            } else {
                errorText1.setText(text);
                errorText1.setVisible(true);
            }
        });
    }

    private void clearErrors() {
        errorText.setVisible(false);
        errorText1.setVisible(false);
    }

    @FXML
    public void signUp(ActionEvent event) throws IOException {
        clearErrors();
        String fullName = nameTextField.getText();
        String username = userTextField.getText();
        String email = emailTextField.getText();
        String phoneNumber = phoneTextField.getText();
        String password = passTextField.getText();
        String confirmPass = confirmPassTextField.getText();
        LocalDate localDate = bday.getValue();
        Date birthdayDate = null;
        if (localDate != null) {
            birthdayDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }

        entryController.signUp(fullName, username, email, phoneNumber, password, confirmPass, birthdayDate, (errorMessage, user, isAdmin) -> {
            if (errorMessage == null) {
                Platform.runLater(() -> {
                    try {
                        SceneManager.switchScene(event, "HashtagSelector.fxml");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } else {
                putText(0, errorMessage);
            }
        });
    }

    @FXML
    public void login(ActionEvent event) throws IOException {
        clearErrors();
        String text = userTextField1.getText();
        String password = passTextField1.getText();
        entryController.loginUser(text, password, (errorMessage, user, isAdmin) -> {
            if (errorMessage == null) {
                Platform.runLater(() -> {
                    try {
                        if (isAdmin) {
                            SceneManager.switchScene(event, "AdminDashboard.fxml");
                        } else {
                            SceneManager.switchScene(event, "TimeLine.fxml");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } else {
                putText(1, errorMessage);
            }
        });
    }
}