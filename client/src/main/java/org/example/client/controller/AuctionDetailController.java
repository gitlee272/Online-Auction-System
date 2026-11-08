package org.example.client.controller;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.concurrent.CompletionException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.example.client.api.ApiException;
import org.example.client.api.ApiProvider;
import org.example.client.model.AuctionDetail;
import org.example.client.model.BidEntry;
import org.example.client.util.SceneNavigator;
import org.example.client.util.SelectedAuction;

/** Màn hình chi tiết phiên: thông tin, đồng hồ đếm ngược, đặt giá, lịch sử. */
public class AuctionDetailController implements Initializable {

    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label statusLabel;
    @FXML private Label priceLabel;
    @FXML private Label leaderLabel;
    @FXML private Label countdownLabel;
    @FXML private Label messageLabel;
    @FXML private TextField bidField;
    @FXML private Button bidButton;
    @FXML private ListView<String> bidList;

    private LocalDateTime endTime;
    private Timeline clock;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Đồng hồ chạy trên thread giao diện nên không cần Platform.runLater.
        clock = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1), e -> tick()));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
        load();
    }

    private void load() {
        ApiProvider.get()
                .getAuction(SelectedAuction.get())
                .whenComplete((detail, error) -> Platform.runLater(() -> {
                    if (error != null) {
                        messageLabel.setText(readableMessage(error));
                        return;
                    }
                    show(detail);
                }));
    }

    private void show(AuctionDetail d) {
        titleLabel.setText(d.itemName() + " (" + d.itemType() + ")");
        descriptionLabel.setText(d.description() + "  |  Giá khởi điểm: "
                + AuctionDetail.money(d.startingPrice()));
        statusLabel.setText("Trạng thái: " + d.status());
        priceLabel.setText(AuctionDetail.money(d.currentPrice()));
        leaderLabel.setText("Người dẫn đầu: " + (d.leader() == null ? "chưa có" : d.leader()));
        endTime = d.endTime();
        bidList.getItems().clear();
        for (int i = d.bids().size() - 1; i >= 0; i--) {   // mới nhất lên đầu
            BidEntry b = d.bids().get(i);
            bidList.getItems().add(b.time() + "  " + b.bidder() + "  " + AuctionDetail.money(b.amount()));
        }
        bidButton.setDisable(!"RUNNING".equals(d.status()));
        tick();
    }

    private void tick() {
        if (endTime == null) {
            return;
        }
        Duration left = Duration.between(LocalDateTime.now(), endTime);
        if (left.isNegative() || left.isZero()) {
            countdownLabel.setText("Đã kết thúc");
            bidButton.setDisable(true);
            return;
        }
        countdownLabel.setText(String.format("Còn lại: %02d:%02d:%02d",
                left.toHours(), left.toMinutesPart(), left.toSecondsPart()));
    }

    @FXML
    private void onBid() {
        long amount;
        try {
            amount = Long.parseLong(bidField.getText().trim().replace(".", "").replace(",", ""));
        } catch (NumberFormatException e) {
            messageLabel.setText("Giá phải là số nguyên (đồng)");
            return;
        }
        if (amount <= 0) {
            messageLabel.setText("Giá phải lớn hơn 0");
            return;
        }
        messageLabel.setText("");
        bidButton.setDisable(true);
        ApiProvider.get()
                .placeBid(SelectedAuction.get(), amount)
                .whenComplete((detail, error) -> Platform.runLater(() -> {
                    if (error != null) {
                        bidButton.setDisable(false);
                        messageLabel.setText(readableMessage(error));
                        return;
                    }
                    bidField.clear();
                    messageLabel.setText("Đặt giá thành công");
                    show(detail);
                }));
    }

    @FXML
    private void onBack() {
        clock.stop();   // dừng đồng hồ khi rời màn hình
        SceneNavigator.goTo("main.fxml");
    }

    private static String readableMessage(Throwable error) {
        Throwable cause = error instanceof CompletionException && error.getCause() != null
                ? error.getCause()
                : error;
        return cause instanceof ApiException ? cause.getMessage() : "Không thể kết nối tới máy chủ";
    }
}