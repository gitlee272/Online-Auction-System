package org.example.client.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletionException;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.client.api.ApiException;
import org.example.client.api.ApiProvider;
import org.example.client.model.UserInfo;
import org.example.client.util.SceneNavigator;

/** Trang quản trị: danh sách người dùng, khóa hoặc mở khóa. */
public class AdminController implements Initializable {

    @FXML private TableView<UserInfo> userTable;
    @FXML private TableColumn<UserInfo, String> idColumn;
    @FXML private TableColumn<UserInfo, String> nameColumn;
    @FXML private TableColumn<UserInfo, String> emailColumn;
    @FXML private TableColumn<UserInfo, String> roleColumn;
    @FXML private TableColumn<UserInfo, String> activeColumn;
    @FXML private Label messageLabel;

    private final ObservableList<UserInfo> users = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().id())));
        nameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().username()));
        emailColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().email()));
        roleColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().role()));
        activeColumn.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().active() ? "Hoạt động" : "Bị khóa"));
        userTable.setItems(users);
        onRefresh();
    }

    @FXML
    private void onRefresh() {
        messageLabel.setText("Đang tải...");
        ApiProvider.get().listUsers().whenComplete((list, error) -> Platform.runLater(() -> {
            if (error != null) {
                messageLabel.setText(readableMessage(error));
                return;
            }
            users.setAll(list);
            messageLabel.setText(list.size() + " người dùng");
        }));
    }

    @FXML
    private void onToggle() {
        UserInfo selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Hãy chọn một người dùng");
            return;
        }
        if ("ADMIN".equals(selected.role())) {
            messageLabel.setText("Không thể khóa tài khoản Admin");
            return;
        }
        ApiProvider.get()
                .setUserActive(selected.id(), !selected.active())
                .whenComplete((v, error) -> Platform.runLater(() -> {
                    if (error != null) {
                        messageLabel.setText(readableMessage(error));
                        return;
                    }
                    onRefresh();
                }));
    }

    @FXML
    private void onBack() {
        SceneNavigator.goTo("main.fxml");
    }

    private static String readableMessage(Throwable error) {
        Throwable cause = error instanceof CompletionException && error.getCause() != null
                ? error.getCause()
                : error;
        return cause instanceof ApiException ? cause.getMessage() : "Không thể kết nối tới máy chủ";
    }
}