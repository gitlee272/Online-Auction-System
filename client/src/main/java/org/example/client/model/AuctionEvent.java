package org.example.client.model;

import java.time.LocalDateTime;

/** Event realtime server đẩy xuống. Trường không liên quan tới loại event thì là null. */
public record AuctionEvent(
        String type,
        long auctionId,
        Long amount,
        String bidder,
        String time,
        LocalDateTime endTime,
        String winner,
        Long finalPrice) {}