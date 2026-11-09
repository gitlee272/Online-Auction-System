package org.example.exception;

import org.example.enums.BidResponseStatus;

public class AuctionException extends RuntimeException {
    private String errorCode;
    private BidResponseStatus status;

    public AuctionException() {
        super();
    }

    public AuctionException(String message) {
        super(message);
        this.errorCode = "AUCTION_ERROR";
    }

    public AuctionException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AuctionException(BidResponseStatus status, String message) {
        super(message);
        this.status = status;
        this.errorCode = status != null ? status.name() : "BID_ERROR";
    }

    public AuctionException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "INTERNAL_ERROR";
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public BidResponseStatus getStatus() {
        return status;
    }

    public void setStatus(BidResponseStatus status) {
        this.status = status;
    }
}
