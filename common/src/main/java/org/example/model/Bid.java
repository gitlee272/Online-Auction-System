package org.example.model;

import java.time.LocalDateTime;

public class Bid {
    private int id;
    private int auctionId; // Thuộc về phiên đấu giá nào
    private int bidderId;  // Ai là người đặt giá
    private double amount; // Mức giá đặt
    private LocalDateTime timestamp; // Thời gian đặt

    public Bid() {}

    public Bid(int id, int auctionId, int bidderId, double amount, LocalDateTime timestamp) {
        this.id = id;
        this.auctionId = auctionId;
        this.bidderId = bidderId;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    // --- Getters & Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAuctionId() { return auctionId; }
    public void setAuctionId(int auctionId) { this.auctionId = auctionId; }

    public int getBidderId() { return bidderId; }
    public void setBidderId(int bidderId) { this.bidderId = bidderId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
