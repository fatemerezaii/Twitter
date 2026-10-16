package view;

import controller.manager.SessionManager;
import exceptions.UserNotFoundException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import model.Post;
import model.files.File;
import model.files.Image;
import model.files.Video;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class PostingController implements Initializable {

    @FXML
    private AnchorPane aPane;
    @FXML
    private Button postBtn;
    @FXML
    private TextArea textArea;
    @FXML
    private Button importMediaBtn;
    @FXML
    private Label mediaNameLabel;

    private File selectedMedia = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SideBar.fxml"));
            AnchorPane sideBarRoot = loader.load();
            aPane.getChildren().add(sideBarRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void importMedia(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Media File");
        java.io.File file = fileChooser.showOpenDialog(aPane.getScene().getWindow());
        if (file != null) {
            String path = file.toURI().toString();
            String fileName = file.getName();
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            if (isImage(extension)) {
                selectedMedia = new Image(path, getImageFormat(extension));
                mediaNameLabel.setText("📷 " + fileName);
                mediaNameLabel.setStyle("-fx-text-fill: #2ecc71;");
            } else if (isVideo(extension)) {
                selectedMedia = new Video(path, Video.Quality.High, getVideoFormat(extension), 0);
                mediaNameLabel.setText("🎬 " + fileName);
                mediaNameLabel.setStyle("-fx-text-fill: #2ecc71;");
            } else {
                selectedMedia = null;
                mediaNameLabel.setText("❌ Unsupported file type");
                mediaNameLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
        }
    }
    @FXML
    void post(ActionEvent event) {
        String text = textArea.getText();
        if (text == null || text.trim().isEmpty()) {
            mediaNameLabel.setText("Post text cannot be empty!");
            mediaNameLabel.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }
        Post newPost = new Post(0, SessionManager.getCurrentUser().getAccID(), text, new Date());
        newPost.setMedia(selectedMedia);
        controller.PostingController networkController = new controller.PostingController();
        try {
            networkController.createPostAsync(newPost, success -> {
                Platform.runLater(() -> {
                    if (success) {
                        textArea.clear();
                        selectedMedia = null;
                        mediaNameLabel.setText("Post created successfully!");
                        mediaNameLabel.setStyle("-fx-text-fill: #2ecc71;");
                    } else {
                        mediaNameLabel.setText("Failed to create post!");
                        mediaNameLabel.setStyle("-fx-text-fill: #e74c3c;");
                    }
                });
            });
        } catch (UserNotFoundException e) {
            mediaNameLabel.setText(e.getMessage());
            mediaNameLabel.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    private boolean isImage(String ext) { return ext.matches("png|jpg|jpeg|gif|bmp|webp"); }
    private boolean isVideo(String ext) { return ext.matches("mp4|avi|mov|mkv|wmv|flv|webm"); }

    private Image.ImgFormat getImageFormat(String ext) {
        return ext.equals("png") ? Image.ImgFormat.PNG : Image.ImgFormat.JPEG;
    }

    private Video.VideoFormat getVideoFormat(String ext) {
        return ext.equals("mp4") ? Video.VideoFormat.MP4 : Video.VideoFormat.MKV;
    }
}