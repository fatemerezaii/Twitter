package view;

import controller.manager.SceneManager;
import controller.manager.SessionManager;
import controller.ReportController;
import exceptions.NetworkException;
import exceptions.ReportNotFoundException;
import exceptions.UserNotFoundException;
import model.accounts.users.User;
import model.Report;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ReportManagerController implements Initializable {

    @FXML private ListView<Report> listView;
    @FXML private Button backBtn;

    private ReportController reportController = new ReportController();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listView.setCellFactory(lv -> new ReportCell());
        loadReports();
    }

    private void loadReports() {

        try {

            List<Report> pendingReports =
                    reportController.pendingReports();

            listView.getItems().setAll(pendingReports);

        } catch (NetworkException e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();

        }

    }

    private class ReportCell extends ListCell<Report> {
        private final HBox hBox = new HBox(10);
        private final Label username = new Label();
        private final Label explanationPreview = new Label();
        private final Button viewPostBtn = new Button("View Post");
        private final Button viewUserBtn = new Button("View User");
        private final Button viewExplanationBtn = new Button("View Report Text");
        private final Button rejectBtn = new Button("Reject");
        private final Button confirmBtn = new Button("Confirm");

        public ReportCell() {
            hBox.setAlignment(Pos.CENTER_LEFT);
            username.setPrefWidth(120);
            explanationPreview.setPrefWidth(180);
            hBox.getChildren().addAll(username, explanationPreview, viewPostBtn, viewUserBtn, viewExplanationBtn, rejectBtn, confirmBtn);
        }

        @Override
        protected void updateItem(Report report, boolean empty) {

            super.updateItem(report, empty);

            if (empty || report == null) {
                setGraphic(null);
                return;
            }

            String reporterUsername;

            try {

                User reporterUser =
                        reportController.getUserById(
                                report.getReporterID()
                        );

                reporterUsername = reporterUser.getUsername();

            } catch (UserNotFoundException | NetworkException e) {

                reporterUsername = "unknown";

            }

            username.setText("@" + reporterUsername);

            String exp = report.getExplanation();

            explanationPreview.setText(
                    exp.length() > 20
                            ? exp.substring(0, 20) + "..."
                            : exp
            );

            boolean isPending =
                    report.getStatus() == Report.Status.WAITING;

            rejectBtn.setVisible(isPending);
            confirmBtn.setVisible(isPending);

            viewPostBtn.setVisible(
                    report.getReportedPostID() > 0
            );

            viewUserBtn.setVisible(
                    report.getReportedUserID() > 0
            );

            viewExplanationBtn.setVisible(true);

            viewPostBtn.setOnAction(e ->
                    showReportedPost(report));

            viewUserBtn.setOnAction(e ->
                    showReportedUser(report));

            viewExplanationBtn.setOnAction(e ->
                    showFullExplanation(report));

            rejectBtn.setOnAction(e -> {

                try {

                    String result =
                            reportController.confirmReport(
                                    report,
                                    false
                            );

                    showAlert("Reject", result);

                    loadReports();

                    listView.refresh();

                } catch (ReportNotFoundException | NetworkException ex) {

                    showAlert("Error", ex.getMessage());

                }

            });

            confirmBtn.setOnAction(e -> {

                Alert confirmDialog =
                        new Alert(Alert.AlertType.CONFIRMATION);

                confirmDialog.setTitle("Confirm Report");
                confirmDialog.setHeaderText("Are you sure?");
                confirmDialog.setContentText(
                        "Do you want to confirm this report? The post/user will be blocked."
                );

                confirmDialog.showAndWait().ifPresent(response -> {

                    if (response == ButtonType.OK) {

                        try {

                            String result =
                                    reportController.confirmReport(
                                            report,
                                            true
                                    );

                            showAlert("Confirm", result);

                            loadReports();

                            listView.refresh();

                        } catch (ReportNotFoundException | NetworkException ex) {

                            showAlert("Error", ex.getMessage());

                        }

                    }

                });

            });

            setGraphic(hBox);
        }

        private void showFullExplanation(Report report) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Report Explanation");
            alert.setHeaderText(null);
            alert.setContentText(report.getExplanation());
            alert.showAndWait();
        }

        private void showReportedUser(Report report) {
            int userId = report.getReportedUserID();
            if (userId <= 0) {
                showAlert("Error", "No user associated with this report.");
                return;
            }
            try {
                PublicViewController.setViewedUserId(userId);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PublicView.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("User Profile");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Could not open user profile.");
            }
        }

        private void showReportedPost(Report report) {
            int postId = report.getReportedPostID();
            if (postId <= 0) {
                showAlert("Error", "No post associated with this report.");
                return;
            }
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PostDetail.fxml"));
                Parent root = loader.load();
                PostDetailController controller = loader.getController();
                controller.loadPostById(postId);
                Stage stage = new Stage();
                stage.setTitle("Post Detail");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Could not open post.");
            }
        }

        private User getCurrentUser() {
            return SessionManager.getCurrentUser();
        }

        private void showAlert(String title, String message) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
    }

    @FXML
    public void back(ActionEvent actionEvent) throws IOException {
        SceneManager.switchScene(actionEvent, "AdminDashboard.fxml");
    }
}
