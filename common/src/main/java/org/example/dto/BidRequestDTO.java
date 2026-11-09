package org.example.dto;

import java.io.Serializable;

public class BidRequestDTO implements Serializable {
    private int auctionId;
    private int bidderId;
    private double bidAmount;
    private double maxAutoBidLimit; // Phục vụ nếu người dùng bật Auto-bid

    public BidRequestDTO() {}

    public BidRequestDTO(int auctionId, int bidderId, double bidAmount) {
        this.auctionId = auctionId;
        this.bidderId = bidderId;
        this.bidAmount = bidAmount;
        this.maxAutoBidLimit = 0.0;
    }

    public BidRequestDTO(int auctionId, int bidderId, double bidAmount, double maxAutoBidLimit) {
        this.auctionId = auctionId;
        this.bidderId = bidderId;
        this.bidAmount = bidAmount;
        this.maxAutoBidLimit = maxAutoBidLimit;
    }

    public int getAuctionId() { return auctionId; }
    public void setAuctionId(int auctionId) { this.auctionId = auctionId; }

    public int getBidderId() { return bidderId; }
    public void setBidderId(int bidderId) { this.bidderId = bidderId; }

    public double getBidAmount() { return bidAmount; }
    public void setBidAmount(double bidAmount) { this.bidAmount = bidAmount; }

    public double getMaxAutoBidLimit() { return maxAutoBidLimit; }
    public void setMaxAutoBidLimit(double maxAutoBidLimit) { this.maxAutoBidLimit = maxAutoBidLimit; }
}
