package org.example.client.api;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.example.client.model.AuctionSummary;
import org.example.client.model.UserSession;
import org.example.client.model.AuctionDetail;

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
}