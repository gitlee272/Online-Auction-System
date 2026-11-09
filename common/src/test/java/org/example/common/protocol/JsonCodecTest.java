package org.example.common.protocol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

class JsonCodecTest {

    @Test
    void request_roundTrip_keepsFields() {
        JsonObject data = new JsonObject();
        data.addProperty("username", "an");
        Request original = new Request("r1", Action.LOGIN, null, data);

        String line = JsonCodec.toJson(original);
        Request parsed = JsonCodec.parseRequest(line);

        assertEquals("r1", parsed.getId());
        assertEquals(Action.LOGIN, parsed.getAction());
        assertEquals("an", parsed.getData().get("username").getAsString());
    }

    @Test
    void serializedMessage_isSingleLine() {
        String line = JsonCodec.toJson(Response.error("r1", ErrorCode.BID_TOO_LOW, "Giá quá thấp"));

        assertFalse(line.contains("\n"));
        assertFalse(line.contains("\r"));
    }

    @Test
    void request_withUnknownAction_hasNullAction() {
        Request parsed = JsonCodec.parseRequest("{\"id\":\"r1\",\"action\":\"FLY_TO_MOON\"}");

        assertNull(parsed.getAction());
    }

    @Test
    void request_withoutData_returnsEmptyObject() {
        Request parsed = JsonCodec.parseRequest("{\"id\":\"r1\",\"action\":\"PING\"}");

        assertTrue(parsed.getData().isEmpty());
    }

    @Test
    void request_withoutId_isRejected() {
        assertThrows(ProtocolException.class, () -> JsonCodec.parseRequest("{\"action\":\"PING\"}"));
    }

    @Test
    void brokenJson_isRejected() {
        assertThrows(ProtocolException.class, () -> JsonCodec.parseRequest("{khong phai json"));
    }

    @Test
    void emptyLine_isRejected() {
        assertThrows(ProtocolException.class, () -> JsonCodec.parseRequest("   "));
    }

    @Test
    void tooLongLine_isRejected() {
        String longLine = "x".repeat(JsonCodec.MAX_LINE_LENGTH + 1);

        assertThrows(ProtocolException.class, () -> JsonCodec.parseRequest(longLine));
    }

    @Test
    void response_ok_hasNoErrorCode() {
        Response response = Response.ok("r1");

        assertTrue(response.isOk());
        assertNull(response.getCode());
    }

    @Test
    void response_error_roundTrip_keepsCodeAndMessage() {
        String line = JsonCodec.toJson(Response.error("r2", ErrorCode.AUCTION_CLOSED, "Phiên đã đóng"));

        Response parsed = JsonCodec.toResponse(JsonCodec.parseObject(line));

        assertFalse(parsed.isOk());
        assertEquals(ErrorCode.AUCTION_CLOSED, parsed.getCode());
        assertEquals("Phiên đã đóng", parsed.getMessage());
    }

    @Test
    void event_isDistinguishedFromResponse() {
        JsonObject data = new JsonObject();
        data.addProperty("auctionId", 5);
        String eventLine = JsonCodec.toJson(new Event(EventType.NEW_BID, data));
        String responseLine = JsonCodec.toJson(Response.ok("r1"));

        assertTrue(JsonCodec.isEvent(JsonCodec.parseObject(eventLine)));
        assertFalse(JsonCodec.isEvent(JsonCodec.parseObject(responseLine)));
        assertEquals(
                EventType.NEW_BID, JsonCodec.toEvent(JsonCodec.parseObject(eventLine)).getEvent());
    }
}