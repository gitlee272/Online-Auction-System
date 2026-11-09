package org.example.common.protocol;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * Mã hóa và giải mã message. Dùng chung cho client và server để cả hai cùng một cách làm.
 * Gson mặc định không in xuống dòng, nên mỗi message luôn nằm gọn trên một dòng.
 */
public final class JsonCodec {

    /** Độ dài tối đa một dòng (xem docs/protocol.md mục 1). */
    public static final int MAX_LINE_LENGTH = 64 * 1024;

    private static final Gson GSON = new Gson();

    private JsonCodec() {}

    public static Gson gson() {
        return GSON;
    }

    /** Chuyển một đối tượng bất kỳ thành một dòng JSON (không có ký tự xuống dòng). */
    public static String toJson(Object value) {
        return GSON.toJson(value);
    }

    public static Request parseRequest(String line) {
        Request request = parse(line, Request.class);
        if (request.getId() == null || request.getId().isBlank()) {
            throw new ProtocolException("Request thiếu id");
        }
        return request;
    }

    /** Dòng nhận được ở client có thể là Response hoặc Event: đọc thô để phân loại. */
    public static JsonObject parseObject(String line) {
        checkLength(line);
        try {
            return JsonParser.parseString(line).getAsJsonObject();
        } catch (JsonParseException | IllegalStateException e) {
            throw new ProtocolException("Không phải JSON object hợp lệ", e);
        }
    }

    /** True nếu message là event (có trường "event"), ngược lại là response. */
    public static boolean isEvent(JsonObject message) {
        return message.has("event");
    }

    public static Response toResponse(JsonObject message) {
        return GSON.fromJson(message, Response.class);
    }

    public static Event toEvent(JsonObject message) {
        return GSON.fromJson(message, Event.class);
    }

    private static <T> T parse(String line, Class<T> type) {
        checkLength(line);
        try {
            T value = GSON.fromJson(line, type);
            if (value == null) {
                throw new ProtocolException("Dòng rỗng");
            }
            return value;
        } catch (JsonParseException e) {
            throw new ProtocolException("JSON không hợp lệ", e);
        }
    }

    private static void checkLength(String line) {
        if (line == null || line.isBlank()) {
            throw new ProtocolException("Dòng rỗng");
        }
        if (line.length() > MAX_LINE_LENGTH) {
            throw new ProtocolException("Dòng quá dài");
        }
    }
}