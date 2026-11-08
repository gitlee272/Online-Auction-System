package org.example.client.api;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.example.client.model.AuctionSummary;
import org.example.client.model.UserSession;

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
}