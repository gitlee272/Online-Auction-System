package org.example.exception;

import org.example.enums.BidResponseStatus;

public class AuctionNotFoundException extends AuctionException {
    public AuctionNotFoundException(int id) {
        super(
                BidResponseStatus.SYSTEM_ERROR,
                "Khong tim thay du lieu dau gia voi ID: " + id
        );
    }
}

