package org.example.client.model;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/** Chi tiết một phiên đấu giá. */
public record AuctionDetail(
        long id,
        String itemName,
        String description,
        String itemType,
        long startingPrice,
        long currentPrice,
        String leader,
        String status,
        LocalDateTime endTime,
        List<BidEntry> bids) {

    /** Định dạng tiền: 18.500.000 đ. */
    public static String money(long amount) {
        return NumberFormat.getInstance(Locale.of("vi", "VN")).format(amount) + " đ";
    }
}