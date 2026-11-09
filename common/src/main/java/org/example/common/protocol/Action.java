package org.example.common.protocol;

/** Các hành động client có thể gửi. Xem docs/protocol.md mục 4. */
public enum Action {
    PING,
    REGISTER,
    LOGIN,
    LOGOUT,
    LIST_AUCTIONS,
    GET_AUCTION,
    CREATE_AUCTION,
    UPDATE_AUCTION,
    CANCEL_AUCTION,
    SUBSCRIBE,
    UNSUBSCRIBE,
    PLACE_BID,
    SET_AUTO_BID,
    PAY_AUCTION,
    LIST_USERS,
    SET_USER_ACTIVE
}