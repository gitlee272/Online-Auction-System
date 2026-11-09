package org.example.common.protocol;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/** Một phản hồi của server cho đúng một Request. Xem docs/protocol.md mục 2.2. */
public class Response {

    public static final String OK = "OK";
    public static final String ERROR = "ERROR";

    private String id;
    private String status;
    private ErrorCode code;
    private String message;
    private JsonElement data;

    /** Constructor rỗng cho Gson. */
    public Response() {}

    /** Phản hồi thành công kèm dữ liệu. */
    public static Response ok(String id, JsonElement data) {
        Response r = new Response();
        r.id = id;
        r.status = OK;
        r.data = data == null ? new JsonObject() : data;
        return r;
    }

    /** Phản hồi thành công không có dữ liệu (data là {}). */
    public static Response ok(String id) {
        return ok(id, new JsonObject());
    }

    /** Phản hồi lỗi. */
    public static Response error(String id, ErrorCode code, String message) {
        return error(id, code, message, null);
    }

    /** Phản hồi lỗi kèm thông tin bổ sung (ví dụ minBid cho BID_TOO_LOW). */
    public static Response error(String id, ErrorCode code, String message, JsonElement data) {
        Response r = new Response();
        r.id = id;
        r.status = ERROR;
        r.code = code;
        r.message = message;
        r.data = data;
        return r;
    }

    public boolean isOk() {
        return OK.equals(status);
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public ErrorCode getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public JsonElement getData() {
        return data;
    }
}