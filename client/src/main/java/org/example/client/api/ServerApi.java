package org.example.client.api;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.example.client.model.AuctionSummary;
import org.example.client.model.UserSession;
import org.example.client.model.AuctionDetail;
import org.example.client.model.NewAuctionRequest;
import org.example.client.model.UserInfo;
import java.util.function.Consumer;
import org.example.client.model.AuctionEvent;
/**
 * Hợp đồng giữa giao diện và server. Controller chỉ gọi interface này, nên đổi từ
 * MockServerApi sang SocketServerApi mà không phải sửa controller.
 * Mọi phương thức chạy bất đồng bộ, KHÔNG chặn thread giao diện.
 */
public interface ServerApi {

    CompletableFuture<UserSession> login(String username, String password);

    CompletableFuture<UserSession> register(
            String username, String password, String email, String role);

    CompletableFuture<List<AuctionSummary>> listAuctions();
    CompletableFuture<AuctionDetail> getAuction(long auctionId);

    /** Đặt giá. Trả về chi tiết phiên sau khi đặt. Ném ApiException BID_TOO_LOW, AUCTION_CLOSED. */
    CompletableFuture<AuctionDetail> placeBid(long auctionId, long amount);
    /** Tạo phiên đấu giá mới (chỉ Seller). Trả về id phiên. */
    CompletableFuture<Long> createAuction(NewAuctionRequest request);
    /** Danh sách người dùng (chỉ Admin). */
    CompletableFuture<List<UserInfo>> listUsers();

    /** Khóa hoặc mở khóa người dùng (chỉ Admin). */
    CompletableFuture<Void> setUserActive(long userId, boolean active);

    /** Hủy phiên đấu giá (Admin hoặc Seller chủ phiên). */
    CompletableFuture<Void> cancelAuction(long auctionId);
    CompletableFuture<Void> subscribe(long auctionId);

    CompletableFuture<Void> unsubscribe(long auctionId);

    /** Nơi nhận event realtime; null để bỏ. Được gọi ở thread khác thread giao diện. */
    void setEventListener(Consumer<AuctionEvent> listener);
}