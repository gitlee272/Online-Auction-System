package org.example.client.api;

/** Lỗi nghiệp vụ trả về từ server (hoặc từ mock). Mã lỗi theo docs/protocol.md. */
public class ApiException extends RuntimeException {

    private final String code;

    public ApiException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}