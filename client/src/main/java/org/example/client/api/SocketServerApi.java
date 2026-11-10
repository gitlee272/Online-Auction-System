package org.example.client.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.example.client.model.AuctionDetail;
import org.example.client.model.AuctionSummary;
import org.example.client.model.BidEntry;
import org.example.client.model.NewAuctionRequest;
import org.example.client.model.UserInfo;
import org.example.client.model.UserSession;
import org.example.client.util.SessionContext;
import org.example.common.protocol.Action;
import org.example.common.protocol.Response;
import java.util.function.Consumer;
import org.example.client.model.AuctionEvent;
import org.example.common.protocol.Event;

/** Cài đặt ServerApi nói chuyện với server thật theo docs/protocol.md. */
public class SocketServerApi implements ServerApi {

    private final ServerConnection connection;


    public ServerConnection connection() {
        return connection;
    }

    @Override
    public CompletableFuture<UserSession> login(String username, String password) {
        JsonObject d = new JsonObject();
        d.addProperty("username", username);
        d.addProperty("password", password);
        return call(Action.LOGIN, d, false).thenApply(SocketServerApi::toSession);
    }

    @Override
    public CompletableFuture<UserSession> register(
            String username, String password, String email, String role) {
        JsonObject d = new JsonObject();
        d.addProperty("username", username);
        d.addProperty("password", password);
        d.addProperty("email", email);
        d.addProperty("role", role);
        return call(Action.REGISTER, d, false).thenApply(SocketServerApi::toSession);
    }

    @Override
    public CompletableFuture<List<AuctionSummary>> listAuctions() {
        return call(Action.LIST_AUCTIONS, new JsonObject(), true).thenApply(r -> {
            List<AuctionSummary> list = new ArrayList<>();
            for (JsonElement e : r.getData().getAsJsonObject().getAsJsonArray("auctions")) {
                JsonObject o = e.getAsJsonObject();
                list.add(new AuctionSummary(
                        o.get("id").getAsLong(),
                        o.get("itemName").getAsString(),
                        o.get("currentPrice").getAsLong(),
                        o.get("status").getAsString(),
                        o.get("endTime").getAsString().replace('T', ' ')));
            }
            return list;
        });
    }

    @Override
    public CompletableFuture<AuctionDetail> getAuction(long auctionId) {
        JsonObject d = new JsonObject();
        d.addProperty("auctionId", auctionId);
        return call(Action.GET_AUCTION, d, true).thenApply(SocketServerApi::toDetail);
    }

    @Override
    public CompletableFuture<AuctionDetail> placeBid(long auctionId, long amount) {
        JsonObject d = new JsonObject();
        d.addProperty("auctionId", auctionId);
        d.addProperty("amount", amount);
        return call(Action.PLACE_BID, d, true).thenApply(SocketServerApi::toDetail);
    }

    @Override
    public CompletableFuture<Long> createAuction(NewAuctionRequest q) {
        JsonObject d = new JsonObject();
        d.addProperty("itemName", q.itemName());
        d.addProperty("description", q.description());
        d.addProperty("itemType", q.itemType().toUpperCase());   // Electronics -> ELECTRONICS
        d.addProperty("condition", q.condition());
        d.addProperty("startingPrice", q.startingPrice());
        d.addProperty("startTime", q.startTime().toString());
        d.addProperty("endTime", q.endTime().toString());
        return call(Action.CREATE_AUCTION, d, true)
                .thenApply(r -> r.getData().getAsJsonObject().get("auctionId").getAsLong());
    }

    @Override
    public CompletableFuture<List<UserInfo>> listUsers() {
        return call(Action.LIST_USERS, new JsonObject(), true).thenApply(r -> {
            List<UserInfo> list = new ArrayList<>();
            for (JsonElement e : r.getData().getAsJsonObject().getAsJsonArray("users")) {
                JsonObject o = e.getAsJsonObject();
                list.add(new UserInfo(
                        o.get("id").getAsLong(),
                        o.get("username").getAsString(),
                        o.get("email").getAsString(),
                        o.get("role").getAsString(),
                        o.get("active").getAsBoolean()));
            }
            return list;
        });
    }

    @Override
    public CompletableFuture<Void> setUserActive(long userId, boolean active) {
        JsonObject d = new JsonObject();
        d.addProperty("userId", userId);
        d.addProperty("active", active);
        return call(Action.SET_USER_ACTIVE, d, true).thenApply(r -> null);
    }

    @Override
    public CompletableFuture<Void> cancelAuction(long auctionId) {
        JsonObject d = new JsonObject();
        d.addProperty("auctionId", auctionId);
        return call(Action.CANCEL_AUCTION, d, true).thenApply(r -> null);
    }

    /** Gửi request; nếu status ERROR thì đổi thành ApiException với mã và thông điệp của server. */
    private CompletableFuture<Response> call(Action action, JsonObject data, boolean needToken) {
        String token = needToken && SessionContext.get() != null ? SessionContext.get().token() : null;
        return connection.send(action, token, data).thenApply(r -> {
            if (!r.isOk()) {
                throw new ApiException(ServerConnection.codeName(r.getCode()), r.getMessage());
            }
            return r;
        });
    }

    private static UserSession toSession(Response r) {
        JsonObject o = r.getData().getAsJsonObject();
        return new UserSession(
                o.get("token").getAsString(),
                o.get("userId").getAsLong(),
                o.get("username").getAsString(),
                o.get("role").getAsString());
    }

    private static AuctionDetail toDetail(Response r) {
        JsonObject o = r.getData().getAsJsonObject();
        List<BidEntry> bids = new ArrayList<>();
        JsonArray arr = o.getAsJsonArray("bids");
        if (arr != null) {
            for (JsonElement e : arr) {
                JsonObject b = e.getAsJsonObject();
                bids.add(new BidEntry(
                        b.get("bidder").getAsString(),
                        b.get("amount").getAsLong(),
                        b.get("time").getAsString()));
            }
        }
        JsonElement leader = o.get("leader");
        return new AuctionDetail(
                o.get("id").getAsLong(),
                o.get("itemName").getAsString(),
                o.get("description").getAsString(),
                o.get("itemType").getAsString(),
                o.get("startingPrice").getAsLong(),
                o.get("currentPrice").getAsLong(),
                leader == null || leader.isJsonNull() ? null : leader.getAsString(),
                o.get("status").getAsString(),
                LocalDateTime.parse(o.get("endTime").getAsString()),
                bids);
    }
    private volatile Consumer<AuctionEvent> auctionListener = e -> {};

    public SocketServerApi(ServerConnection connection) {
        this.connection = connection;
        connection.setEventListener(this::onServerEvent);
    }

    @Override
    public CompletableFuture<Void> subscribe(long auctionId) {
        JsonObject d = new JsonObject();
        d.addProperty("auctionId", auctionId);
        return call(Action.SUBSCRIBE, d, true).thenApply(r -> null);
    }

    @Override
    public CompletableFuture<Void> unsubscribe(long auctionId) {
        JsonObject d = new JsonObject();
        d.addProperty("auctionId", auctionId);
        return call(Action.UNSUBSCRIBE, d, true).thenApply(r -> null);
    }

    @Override
    public void setEventListener(Consumer<AuctionEvent> listener) {
        this.auctionListener = listener == null ? e -> {} : listener;
    }

    private void onServerEvent(Event event) {
        if (event.getEvent() == null || event.getData() == null || !event.getData().isJsonObject()) {
            return;
        }
        JsonObject o = event.getData().getAsJsonObject();
        String end = text(o, "endTime");
        auctionListener.accept(new AuctionEvent(
                event.getEvent().name(),
                o.get("auctionId").getAsLong(),
                number(o, "amount"),
                text(o, "bidder"),
                text(o, "time"),
                end == null ? null : LocalDateTime.parse(end),
                text(o, "winner"),
                number(o, "finalPrice")));
    }

    private static String text(JsonObject o, String key) {
        JsonElement e = o.get(key);
        return e == null || e.isJsonNull() ? null : e.getAsString();
    }

    private static Long number(JsonObject o, String key) {
        JsonElement e = o.get(key);
        return e == null || e.isJsonNull() ? null : e.getAsLong();
    }
}