package org.example.client.model;

import java.text.NumberFormat;
import java.util.Locale;

/** Dòng hiển thị trong danh sách phiên đấu giá. Số tiền tính bằng đồng. */
public record AuctionSummary(
        long id, String itemName, long currentPrice, String status, String endTime) {

    /** Giá hiển thị dạng 18.500.000 đ. */
    public String priceText() {
        return NumberFormat.getInstance(Locale.of("vi", "VN")).format(currentPrice) + " đ";
    }
}