package org.example.dto;

import org.example.enums.BidResponseStatus;
import java.io.Serializable;

public class BidResponseDTO implements Serializable {
    private boolean success;
    private BidResponseStatus status;
    private String message;
    private double currentHighestBid;
    private int highestBidderId;
    private String updatedEndTime;

    public BidResponseDTO() {}

    public BidResponseDTO(
            boolean success,
            BidResponseStatus status,
            String message,
            double currentHighestBid,
            int highestBidderId,
            String updatedEndTime
    ) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.currentHighestBid = currentHighestBid;
        this.highestBidderId = highestBidderId;
        this.updatedEndTime = updatedEndTime;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public BidResponseStatus getStatus() { return status; }
    public void setStatus(BidResponseStatus status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public double getCurrentHighestBid() { return currentHighestBid; }
    public void setCurrentHighestBid(double currentHighestBid) { this.currentHighestBid = currentHighestBid; }

    public int getHighestBidderId() { return highestBidderId; }
    public void setHighestBidderId(int highestBidderId) { this.highestBidderId = highestBidderId; }

    public String getUpdatedEndTime() { return updatedEndTime; }
    public void setUpdatedEndTime(String updatedEndTime) { this.updatedEndTime = updatedEndTime; }
}
