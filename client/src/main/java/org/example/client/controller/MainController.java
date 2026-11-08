package org.example.client.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletionException;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.example.client.api.ApiException;
import org.example.client.api.ApiProvider;
import org.example.client.model.AuctionSummary;
import org.example.client.model.UserSession;
import org.example.client.util.SceneNavigator;
import org.example.client.util.SessionContext;
import org.example.client.util.SelectedAuction;
import javafx.scene.control.Button;

/** Trang chính: danh sách phiên đấu giá, có lọc theo trạng thái và tìm theo tên. */
public class MainController implements Initializable {

    private static final String ALL = "Tất cả";

    @FXML private Label welcomeLabel;
    @FXML private Label statusLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TableView<AuctionSummary> auctionTable;
    @FXML private TableColumn<AuctionSummary, String> nameColumn;
    @FXML private TableColumn<AuctionSummary, String> priceColumn;
    @FXML private TableColumn<AuctionSummary, String> statusColumn;
    @FXML private TableColumn<AuctionSummary, String> endColumn;
    @FXML private Button createButton;

    private final ObservableList<AuctionSummary> auctions = FXCollections.observableArrayList();
    private final FilteredList<AuctionSummary> filtered = new FilteredList<>(auctions, a -> true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UserSession session = SessionContext.get();
        welcomeLabel.setText("Xin chào " + session.username() + " (" + session.role() + ")");
        boolean isSeller = "SELLER".equals(session.role());
        createButton.setVisible(isSeller);
        createButton.setManaged(isSeller);   // ẩn thì không chiếm chỗ
        nameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().itemName()));
        priceColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().priceText()));
        statusColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().status()));
        endColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().endTime()));
        auctionTable.setItems(filtered);

        statusFilter.setItems(
                FXCollections.observableArrayList(ALL, "OPEN", "RUNNING", "FINISHED", "PAID", "CANCELED"));
        statusFilter.setValue(ALL);

        // Lọc ngay khi gõ hoặc đổi trạng thái, không cần gọi lại server.
        searchField.textProperty().addListener((obs, o, n) -> applyFilter());
        statusFilter.valueProperty().addListener((obs, o, n) -> applyFilter());

        onRefresh();
        // Nhấp đúp một dòng để mở chi tiết phiên.
        auctionTable.setOnMouseClicked(e -> {
            AuctionSummary row = auctionTable.getSelectionModel().getSelectedItem();
            if (e.getClickCount() == 2 && row != null) {
                SelectedAuction.set(row.id());
                SceneNavigator.goTo("auction-detail.fxml");
            }
        });
    }

    @FXML
    private void onRefresh() {
        statusLabel.setText("Đang tải...");
        ApiProvider.get()
                .listAuctions()
                .whenComplete(
                        (list, error) ->
                                Platform.runLater(
                                        () -> {
                                            if (error != null) {
                                                statusLabel.setText(readableMessage(error));
                                                return;
                                            }
                                            show(list);
                                        }));
    }

    private void show(List<AuctionSummary> list) {
        auctions.setAll(list);
        statusLabel.setText(list.size() + " phiên");
    }

    private void applyFilter() {
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        String status = statusFilter.getValue();
        filtered.setPredicate(
                a ->
                        (ALL.equals(status) || a.status().equals(status))
                                && a.itemName().toLowerCase().contains(keyword));
    }

    @FXML
    private void onLogout() {
        SessionContext.clear();
        SceneNavigator.goTo("login.fxml");
    }

    private static String readableMessage(Throwable error) {
        Throwable cause = error instanceof CompletionException && error.getCause() != null
                ? error.getCause()
                : error;
        return cause instanceof ApiException ? cause.getMessage() : "Không thể kết nối tới máy chủ";
    }
    @FXML
    private void onCreateAuction() {
        SceneNavigator.goTo("create-auction.fxml");
    }
}