package org.example.exception;

import org.example.enums.BidResponseStatus;

public class InvalidBidException extends AuctionException {
    public InvalidBidException(String reason) {
        super(
                BidResponseStatus.USER_NOT_ELIGIBLE,
                "Lenh dat gia khong hop le: " + reason
        );
    }
}
