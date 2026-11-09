package org.example.common.protocol;

import com.google.gson.JsonElement;

/** Thông báo server chủ động đẩy xuống. Không có id. Xem docs/protocol.md mục 2.3. */
public class Event {

    private EventType event;
    private JsonElement data;

    /** Constructor rỗng cho Gson. */
    public Event() {}

    public Event(EventType event, JsonElement data) {
        this.event = event;
        this.data = data;
    }

    public EventType getEvent() {
        return event;
    }

    public JsonElement getData() {
        return data;
    }
}