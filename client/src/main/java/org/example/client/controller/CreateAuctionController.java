package org.example.client.controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;
import java.util.concurrent.CompletionException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.example.client.api.ApiException;
import org.example.client.api.ApiProvider;
import org.example.client.model.NewAuctionRequest;
import org.example.client.util.SceneNavigator;

/** Form tạo phiên đấu giá (dành cho Seller). */
public class CreateAuctionController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<String> typeBox;
    @FXML private ComboBox<String> conditionBox;
    @FXML private TextField priceField;
    @FXML private DatePicker startDate;
    @FXML private TextField startTime;
    @FXML private DatePicker endDate;
    @FXML private TextField endTime;
    @FXML private Label messageLabel;
    @FXML private Button createButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeBox.setItems(FXCollections.observableArrayList("Electronics", "Art", "Vehicle"));
        typeBox.setValue("Electronics");
        conditionBox.setItems(FXCollections.observableArrayList("NEW", "LIKE_NEW", "USED"));
        conditionBox.setValue("NEW");
        startDate.setValue(LocalDate.now());
        startTime.setText("20:00");
        endDate.setValue(LocalDate.now().plusDays(1));
        endTime.setText("20:00");
    }

    @FXML
    private void onCreate() {
        NewAuctionRequest request;
        try {
            request = readForm();
        } catch (IllegalArgumentException e) {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText(e.getMessage());
            return;
        }

        messageLabel.setText("");
        createButton.setDisable(true);
        ApiProvider.get()
                .createAuction(request)
                .whenComplete((id, error) -> Platform.runLater(() -> {
                    createButton.setDisable(false);
                    if (error != null) {
                        messageLabel.setStyle("-fx-text-fill: red;");
                        messageLabel.setText(readableMessage(error));
                        return;
                    }
                    messageLabel.setStyle("-fx-text-fill: green;");
                    messageLabel.setText("Đã tạo phiên #" + id);
                }));
    }

    /** Đọc và kiểm tra form; ném IllegalArgumentException kèm thông báo tiếng Việt nếu sai. */
    private NewAuctionRequest readForm() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập tên sản phẩm");
        }
        long price;
        try {
            price = Long.parseLong(priceField.getText().trim().replace(".", "").replace(",", ""));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Giá khởi điểm phải là số nguyên (đồng)");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Giá khởi điểm phải lớn hơn 0");
        }
        LocalDateTime start = parse(startDate, startTime, "bắt đầu");
        LocalDateTime end = parse(endDate, endTime, "kết thúc");
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("Thời gian kết thúc phải sau thời gian bắt đầu");
        }
        return new NewAuctionRequest(name, descriptionArea.getText().trim(),
                typeBox.getValue(), conditionBox.getValue(), price, start, end);
    }

    private static LocalDateTime parse(DatePicker date, TextField time, String label) {
        if (date.getValue() == null) {
            throw new IllegalArgumentException("Vui lòng chọn ngày " + label);
        }
        try {
            return LocalDateTime.of(date.getValue(), LocalTime.parse(time.getText().trim()));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Giờ " + label + " phải có dạng HH:mm, ví dụ 20:00");
        }
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