module com.example.demo2 {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.sql;
    requires java.desktop;

    opens example to javafx.fxml;
    opens view to javafx.fxml;
    opens controller to javafx.fxml;

    exports example;
}