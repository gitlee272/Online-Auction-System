package org.example.client.api;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.example.client.model.AuctionSummary;
import org.example.client.model.UserSession;
import org.example.client.model.AuctionDetail;
import org.example.client.model.BidEntry;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.example.client.model.NewAuctionRequest;
import org.example.client.model.UserInfo;

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
    @Override
    public CompletableFuture<Long> createAuction(NewAuctionRequest request) {
        return CompletableFuture.supplyAsync(
                () -> {
                    sleep();
                    String role = org.example.client.util.SessionContext.get().role();
                    if (!"SELLER".equals(role)) {
                        throw new ApiException("NOT_AUTHORIZED", "Chỉ Seller mới được tạo phiên đấu giá");
                    }
                    return 100L;
                });
    }
    private final List<UserInfo> mockUsers = new ArrayList<>(List.of(
            new UserInfo(1, "admin1", "admin1@mail.com", "ADMIN", true),
            new UserInfo(2, "seller1", "seller1@mail.com", "SELLER", true),
            new UserInfo(3, "an", "an@mail.com", "BIDDER", true),
            new UserInfo(4, "binh", "binh@mail.com", "BIDDER", false)));

    private static void requireAdmin() {
        if (!"ADMIN".equals(org.example.client.util.SessionContext.get().role())) {
            throw new ApiException("NOT_AUTHORIZED", "Chỉ Admin mới được thực hiện");
        }
    }

    @Override
    public CompletableFuture<List<UserInfo>> listUsers() {
        return CompletableFuture.supplyAsync(() -> {
            sleep();
            requireAdmin();
            synchronized (mockUsers) {
                return List.copyOf(mockUsers);
            }
        });
    }

    @Override
    public CompletableFuture<Void> setUserActive(long userId, boolean active) {
        return CompletableFuture.supplyAsync(() -> {
            sleep();
            requireAdmin();
            synchronized (mockUsers) {
                for (int i = 0; i < mockUsers.size(); i++) {
                    UserInfo u = mockUsers.get(i);
                    if (u.id() == userId) {
                        mockUsers.set(i, new UserInfo(u.id(), u.username(), u.email(), u.role(), active));
                    }
                }
            }
            return null;
        });
    }

    @Override
    public CompletableFuture<Void> cancelAuction(long auctionId) {
        return CompletableFuture.supplyAsync(() -> {
            sleep();
            requireAdmin();
            return null;
        });
    }
}