package org.example.client.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletionException;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.client.api.ApiException;
import org.example.client.api.ApiProvider;
import org.example.client.util.SceneNavigator;
import org.example.client.util.SessionContext;

/** Điều khiển màn hình đăng ký. */
public class RegisterController implements Initializable {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final int MIN_PASSWORD_LENGTH = 6;

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmField;
    @FXML private ComboBox<String> roleBox;
    @FXML private Label errorLabel;
    @FXML private Button registerButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Admin không cho tự đăng ký; chỉ có Bidder và Seller.
        roleBox.setItems(FXCollections.observableArrayList("BIDDER", "SELLER"));
        roleBox.setValue("BIDDER");
    }

    @FXML
    private void onRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirm = confirmField.getText();
        String role = roleBox.getValue();

        // Kiểm tra đầu vào ở client (server vẫn phải kiểm tra lại).
        String problem = validate(username, email, password, confirm);
        if (problem != null) {
            errorLabel.setText(problem);
            return;
        }

        errorLabel.setText("");
        registerButton.setDisable(true);

        ApiProvider.get()
                .register(username, password, email, role)
                .whenComplete(
                        (session, error) ->
                                Platform.runLater(
                                        () -> {
                                            registerButton.setDisable(false);
                                            if (error != null) {
                                                errorLabel.setText(readableMessage(error));
                                                return;
                                            }
                                            SessionContext.set(session);
                                            SceneNavigator.goTo("main.fxml");
                                        }));
    }

    @FXML
    private void onBack() {
        SceneNavigator.goTo("login.fxml");
    }

    private static String validate(
            String username, String email, String password, String confirm) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return "Vui lòng nhập đủ thông tin";
        }
        if (!EMAIL.matcher(email).matches()) {
            return "Email không hợp lệ";
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return "Mật khẩu tối thiểu " + MIN_PASSWORD_LENGTH + " ký tự";
        }
        if (!password.equals(confirm)) {
            return "Mật khẩu nhập lại không khớp";
        }
        return null;
    }

    private static String readableMessage(Throwable error) {
        Throwable cause = error instanceof CompletionException && error.getCause() != null
                ? error.getCause()
                : error;
        return cause instanceof ApiException ? cause.getMessage() : "Không thể kết nối tới máy chủ";
    }
}