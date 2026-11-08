package org.example.client.api;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.example.client.model.AuctionSummary;
import org.example.client.model.UserSession;
import org.example.client.model.AuctionDetail;
import org.example.client.model.BidEntry;
import java.time.LocalDateTime;
import java.util.ArrayList;

/** Cài đặt giả: trả dữ liệu cứng, giả lập độ trễ mạng. Dùng khi server chưa xong. */
public class MockServerApi implements ServerApi {

    private static final long FAKE_DELAY_MS = 400;

    @Override
    public CompletableFuture<UserSession> login(String username, String password) {
        return CompletableFuture.supplyAsync(
                () -> {
                    sleep();
                    if (!"123456".equals(password)) {
                        throw new ApiException("INVALID_CREDENTIALS", "Sai tên đăng nhập hoặc mật khẩu");
                    }
                    String role = username.startsWith("admin") ? "ADMIN"
                            : username.startsWith("seller") ? "SELLER" : "BIDDER";
                    return new UserSession("mock-token", 1L, username, role);
                });
    }

    @Override
    public CompletableFuture<UserSession> register(
            String username, String password, String email, String role) {
        return CompletableFuture.supplyAsync(
                () -> {
                    sleep();
                    if ("an".equals(username)) {
                        throw new ApiException("USERNAME_TAKEN", "Tên đăng nhập đã tồn tại");
                    }
                    return new UserSession("mock-token", 2L, username, role);
                });
    }

    @Override
    public CompletableFuture<List<AuctionSummary>> listAuctions() {
        return CompletableFuture.supplyAsync(
                () -> {
                    sleep();
                    return List.of(
                            new AuctionSummary(1, "iPhone 15 Pro", 18_500_000, "RUNNING", "2026-11-20 20:00"),
                            new AuctionSummary(2, "Tranh sơn dầu", 5_200_000, "RUNNING", "2026-11-21 21:30"),
                            new AuctionSummary(3, "Xe Honda SH 2020", 52_000_000, "OPEN", "2026-11-25 19:00"));
                });
    }

    private static void sleep() {
        try {
            Thread.sleep(FAKE_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    private static final long STEP = 100_000;
    private static final long START = 18_000_000;
    private long mockPrice = 18_500_000;
    private String mockLeader = "binh";
    private final List<BidEntry> mockBids = new ArrayList<>(List.of(
            new BidEntry("an", 18_200_000, "10:01:10"),
            new BidEntry("binh", 18_500_000, "10:03:45")));

    @Override
    public CompletableFuture<AuctionDetail> getAuction(long auctionId) {
        return CompletableFuture.supplyAsync(
                () -> {
                    sleep();
                    return snapshot(auctionId);
                });
    }

    @Override
    public synchronized CompletableFuture<AuctionDetail> placeBid(long auctionId, long amount) {
        return CompletableFuture.supplyAsync(
                () -> {
                    sleep();
                    synchronized (this) {
                        if (amount < mockPrice + STEP) {
                            throw new ApiException("BID_TOO_LOW",
                                    "Giá phải từ " + AuctionDetail.money(mockPrice + STEP) + " trở lên");
                        }
                        mockPrice = amount;
                        mockLeader = org.example.client.util.SessionContext.get().username();
                        mockBids.add(new BidEntry(mockLeader, amount,
                                java.time.LocalTime.now().withNano(0).toString()));
                        return snapshot(auctionId);
                    }
                });
    }

    private synchronized AuctionDetail snapshot(long auctionId) {
        return new AuctionDetail(auctionId, "iPhone 15 Pro", "Máy mới 99%, đầy đủ phụ kiện.",
                "Electronics", START, mockPrice, mockLeader, "RUNNING",
                LocalDateTime.now().plusMinutes(3), List.copyOf(mockBids));
    }
}