package org.example.client.controller;

import java.util.concurrent.CompletionException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.client.api.ApiException;
import org.example.client.api.ApiProvider;
import org.example.client.util.SceneNavigator;
import org.example.client.util.SessionContext;

/** Điều khiển màn hình đăng nhập. */
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    @FXML
    private void onLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Kiểm tra đầu vào ở client (server vẫn phải kiểm tra lại).
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Vui lòng nhập đủ tên đăng nhập và mật khẩu");
            return;
        }

        errorLabel.setText("");
        loginButton.setDisable(true);

        // Gọi bất đồng bộ; kết quả về ở thread khác nên phải dùng Platform.runLater.
        ApiProvider.get()
                .login(username, password)
                .whenComplete(
                        (session, error) ->
                                Platform.runLater(
                                        () -> {
                                            loginButton.setDisable(false);
                                            if (error != null) {
                                                errorLabel.setText(readableMessage(error));
                                                return;
                                            }
                                            SessionContext.set(session);
                                            SceneNavigator.goTo("main.fxml");
                                        }));
    }

    private static String readableMessage(Throwable error) {
        Throwable cause = error instanceof CompletionException && error.getCause() != null
                ? error.getCause()
                : error;
        if (cause instanceof ApiException) {
            return cause.getMessage();
        }
        return "Không thể kết nối tới máy chủ";
    }
}