package org.example.exception;

import org.example.enums.BidResponseStatus;

public class AuctionClosedException extends AuctionException {
    public AuctionClosedException(int auctionId) {
        super(
                BidResponseStatus.AUCTION_CLOSED,
                "Phien dau gia #" + auctionId + " da ket thuc hoac khong con mo."
        );
    }
}

