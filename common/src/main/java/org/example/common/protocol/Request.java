package org.example.common.protocol;

import com.google.gson.JsonObject;

/** Một yêu cầu từ client. Một dòng JSON, xem docs/protocol.md mục 2.1. */
public class Request {

    private String id;
    private Action action;
    private String token;
    private JsonObject data;

    /** Constructor rỗng cho Gson. */
    public Request() {}

    public Request(String id, Action action, String token, JsonObject data) {
        this.id = id;
        this.action = action;
        this.token = token;
        this.data = data == null ? new JsonObject() : data;
    }

    public String getId() {
        return id;
    }

    /** Trả về null nếu action trong JSON không có trong enum (server trả UNKNOWN_ACTION). */
    public Action getAction() {
        return action;
    }

    public String getToken() {
        return token;
    }

    /** Không bao giờ trả về null. */
    public JsonObject getData() {
        return data == null ? new JsonObject() : data;
    }
}