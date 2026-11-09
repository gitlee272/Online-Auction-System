package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

public class AuctionController {

    @FXML
    private TableView<?> tableViewProducts;

    @FXML
    private TableColumn<?, ?> colItemName;

    @FXML
    private TableColumn<?, ?> colCurrentPrice;

    @FXML
    private TableColumn<?, ?> colEndTime;

    // Viết các hàm xử lý sự kiện hoặc đổ dữ liệu lên bảng tại đây
}