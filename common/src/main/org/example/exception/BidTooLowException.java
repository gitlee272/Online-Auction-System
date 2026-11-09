package org.example.exception;

import org.example.enums.BidResponseStatus;

public class BidTooLowException extends AuctionException {
    public BidTooLowException(double currentHighestBid, double offeredBid) {
        super(
                BidResponseStatus.REJECTED_LOW_BID,
                "Gia dat (" + offeredBid + ") qua thap! " +
                        "Phai lon hon muc gia hien tai (" + currentHighestBid + ")."
        );
    }
}