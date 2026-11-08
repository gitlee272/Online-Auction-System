package org.example.client.model;

/** Dòng hiển thị trong danh sách phiên đấu giá. Số tiền tính bằng đồng. */
public record AuctionSummary(
        long id, String itemName, long currentPrice, String status, String endTime) {}