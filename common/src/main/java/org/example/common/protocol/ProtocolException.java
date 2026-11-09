package org.example.common.protocol;

/** Message nhận được không đúng khuôn giao thức. Server đổi thành INVALID_JSON. */
public class ProtocolException extends RuntimeException {

    public ProtocolException(String message) {
        super(message);
    }

    public ProtocolException(String message, Throwable cause) {
        super(message, cause);
    }
}