package org.example.client.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.example.client.model.UserSession;
import org.example.client.util.SceneNavigator;
import org.example.client.util.SessionContext;

/** Trang chính (tạm thời). Sẽ được thay bằng danh sách phiên đấu giá ở nhánh sau. */
public class MainController implements Initializable {

    @FXML private Label welcomeLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UserSession session = SessionContext.get();
        welcomeLabel.setText("Xin chào " + session.username() + " (" + session.role() + ")");
    }

    @FXML
    private void onLogout() {
        SessionContext.clear();
        SceneNavigator.goTo("login.fxml");
    }
}