package org.example.client.model;

import java.time.LocalDateTime;

/** Dữ liệu form tạo phiên đấu giá. Giá tính bằng đồng. */
public record NewAuctionRequest(
        String itemName,
        String description,
        String itemType,
        String condition,
        long startingPrice,
        LocalDateTime startTime,
        LocalDateTime endTime) {}