package org.example.server.service;

import org.example.model.BidTransaction;
import org.example.server.dao.BidDAO;

import java.time.LocalDateTime;
import java.util.List;

public class BidService {
    private final BidDAO bidDAO;

    public BidService() {
        this.bidDAO = new BidDAO();
    }

    // Lấy toàn bộ lịch sử đặt giá của một phiên đấu giá
    public List<BidTransaction> getBidHistory(int auctionId) {
        return bidDAO.getBidsByAuctionId(auctionId);
    }

    // Logic hỗ trợ Auto-bid (Trả giá tự động theo hạn mức của người dùng)
    public boolean processAutoBid(int auctionId, int bidderId, double maxLimitAmount, double currentHighestBid, double incrementStep) {
        // Tính toán mức giá tự động tiếp theo cần đặt
        double nextBidAmount = currentHighestBid + incrementStep;

        // Kiểm tra xem mức giá mới có vượt quá giới hạn (max limit) người dùng cho phép không
        if (nextBidAmount > maxLimitAmount) {
            System.out.println("[BidService] Auto-bid dừng lại vì đã chạm mức giá trần của người dùng ID: " + bidderId);
            return false;
        }

        // Tạo giao dịch bid tự động
        BidTransaction autoBid = new BidTransaction();
        autoBid.setAuctionId(auctionId);
        autoBid.setBidderId(bidderId);
        autoBid.setBidAmount(nextBidAmount);
        autoBid.setBidTime(LocalDateTime.now());

        boolean success = bidDAO.addBid(autoBid);
        if (success) {
            System.out.println("[BidService] Đặt giá tự động thành công cho User #" + bidderId + " với số tiền: " + nextBidAmount);
        }
        return success;
    }
}