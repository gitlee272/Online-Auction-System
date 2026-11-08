package org.example.client.model;

/** Một lượt đặt giá trong lịch sử. Số tiền tính bằng đồng. */
public record BidEntry(String bidder, long amount, String time) {}