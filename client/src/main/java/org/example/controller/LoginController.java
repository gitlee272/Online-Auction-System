package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblMessage;

    @FXML
    protected void handleLoginButtonAction(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            lblMessage.setText("Vui lòng không để trống thông tin!");
            return;
        }

        // Tách biệt logic: Gọi Service hoặc hàm xử lý xác thực
        boolean isValid = authenticateUser(username, password);

        if (isValid) {
            lblMessage.setText("Đăng nhập thành công!");
            // TODO: Chuyển sang màn hình danh sách đấu giá
        } else {
            lblMessage.setText("Sai tài khoản hoặc mật khẩu.");
        }
    }

    private boolean authenticateUser(String username, String password) {
        // Tạm thời kiểm tra tài khoản tĩnh, sau này sẽ tích hợp với Server/Database
        return "admin".equals(username) && "123456".equals(password);
    }
}
