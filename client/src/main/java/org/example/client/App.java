package org.example.client;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.client.util.SceneNavigator;

/** Điểm khởi động ứng dụng JavaFX. */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Online Auction System");
        SceneNavigator.init(stage);
        SceneNavigator.goTo("login.fxml");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}