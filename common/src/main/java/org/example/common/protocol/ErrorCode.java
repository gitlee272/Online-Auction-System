package org.example.common.protocol;

/** Mã lỗi trả về trong Response. Xem docs/protocol.md mục 5. */
public enum ErrorCode {
    INVALID_JSON,
    UNKNOWN_ACTION,
    VALIDATION_ERROR,
    INVALID_CREDENTIALS,
    ACCOUNT_LOCKED,
    USERNAME_TAKEN,
    NOT_AUTHENTICATED,
    NOT_AUTHORIZED,
    AUCTION_NOT_FOUND,
    AUCTION_CLOSED,
    BID_TOO_LOW,
    INVALID_STATE,
    INTERNAL_ERROR
}