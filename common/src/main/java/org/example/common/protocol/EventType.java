package org.example.common.protocol;

/** Các loại event server đẩy xuống. Xem docs/protocol.md mục 6. */
public enum EventType {
    NEW_BID,
    AUCTION_STARTED,
    AUCTION_EXTENDED,
    AUCTION_ENDED,
    AUCTION_CANCELED
}