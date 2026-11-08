package org.example.client.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Chuyển giữa các màn hình (file FXML) trên cùng một cửa sổ. */
public final class SceneNavigator {

    private static final String VIEW_DIR = "/org/example/client/view/";
    private static Stage stage;

    private SceneNavigator() {}

    public static void init(Stage primaryStage) {
        stage = primaryStage;
    }

    /** Chuyển sang màn hình có tên file FXML, ví dụ "login.fxml". */
    public static void goTo(String fxmlName) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource(VIEW_DIR + fxmlName));
            Parent root = loader.load();
            if (stage.getScene() == null) {
                stage.setScene(new Scene(root, 900, 600));
            } else {
                stage.getScene().setRoot(root);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Không mở được màn hình " + fxmlName, e);
        }
    }
}