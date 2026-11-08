package org.example.model;

import org.example.enums.AuctionStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Auction {
    private int id;
    private Item item; // Sản phẩm được đấu giá
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AuctionStatus status;
    private List<Bid> bidHistory; // Danh sách các lượt trả giá
    private Integer winnerId; // ID người chiến thắng (null nếu chưa kết thúc)

    public Auction() {
        this.bidHistory = new ArrayList<>();
        this.status = AuctionStatus.OPEN;
    }

    public Auction(int id, Item item, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.item = item;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = AuctionStatus.OPEN;
        this.bidHistory = new ArrayList<>();
    }

    // Thêm một lượt đặt giá mới vào lịch sử
    public void addBid(Bid bid) {
        this.bidHistory.add(bid);
    }

    // Lấy mức giá cao nhất hiện tại
    public double getHighestBidAmount() {
        if (bidHistory.isEmpty()) {
            return item.getStartingPrice();
        }
        double highest = item.getStartingPrice();
        for (Bid b : bidHistory) {
            if (b.getAmount() > highest) {
                highest = b.getAmount();
            }
        }
        return highest;
    }

    // --- Getters & Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public AuctionStatus getStatus() { return status; }
    public void setStatus(AuctionStatus status) { this.status = status; }

    public List<Bid> getBidHistory() { return bidHistory; }
    public void setBidHistory(List<Bid> bidHistory) { this.bidHistory = bidHistory; }

    public Integer getWinnerId() { return winnerId; }
    public void setWinnerId(Integer winnerId) { this.winnerId = winnerId; }
}

