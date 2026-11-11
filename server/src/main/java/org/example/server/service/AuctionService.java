package org.example.server.service;

import org.example.exception.AuctionClosedException;
import org.example.exception.AuctionNotFoundException;
import org.example.exception.BidTooLowException;
import org.example.exception.InvalidBidException;
import org.example.model.Auction;
import org.example.model.BidTransaction;
import org.example.model.Item;
import org.example.server.dao.AuctionDAO;
import org.example.server.dao.BidDAO;
import org.example.server.dao.ItemDAO;

import java.time.LocalDateTime;

public class AuctionService {
    private final AuctionDAO auctionDAO;
    private final BidDAO bidDAO;
    private final ItemDAO itemDAO;

    public AuctionService() {
        this.auctionDAO = new AuctionDAO();
        this.bidDAO = new BidDAO();
        this.itemDAO = new ItemDAO();
    }

    // Sử dụng synchronized để đảm bảo an toàn luồng (thread-safe) khi nhiều user đặt giá đồng thời
    public synchronized boolean placeBid(int auctionId, int bidderId, double bidAmount) {
        Auction auction = auctionDAO.getAuctionById(auctionId);

        // 1. Kiểm tra phiên đấu giá có tồn tại không
        if (auction == null) {
            throw new AuctionNotFoundException(auctionId);
        }

        LocalDateTime now = LocalDateTime.now();

        // 2. Kiểm tra thời gian kết thúc phiên
        if (now.isAfter(auction.getEndTime())) {
            throw new AuctionClosedException(auctionId);
        }

        // 3. Kiểm tra không cho phép người đang dẫn đầu tự đặt giá đè lên chính mình
        if (auction.getWinnerId() != null && auction.getWinnerId() == bidderId) {
            throw new InvalidBidException("Ban dang la nguoi giu muc gia cao nhat cua phien nay roi!");
        }

        // 4. Kiểm tra không cho phép người bán (Seller) tự trả giá ảo cho sản phẩm của mình
        Item item = itemDAO.getItemById(auction.getItemId());
        if (item != null && item.getSellerId() == bidderId) {
            throw new InvalidBidException("Nguoi ban khong duoc phep tu dat gia cho vat pham cua chinh minh!");
        }

        // 5. Kiểm tra mức giá đặt phải cao hơn giá cao nhất hiện tại
        double currentHighest = auction.getCurrentHighestBid();
        if (bidAmount <= currentHighest) {
            throw new BidTooLowException(currentHighest, bidAmount);
        }

        // 6. Tính năng Anti-Sniping (Chống bắn tỉa phút chót)
        // Nếu đặt giá trong vòng 5 phút cuối, tự động gia hạn thêm 5 phút
        if (now.plusMinutes(5).isAfter(auction.getEndTime())) {
            LocalDateTime newEndTime = auction.getEndTime().plusMinutes(5);
            auction.setEndTime(newEndTime);
            auctionDAO.updateAuctionEndTime(auctionId, newEndTime);
            System.out.println("[AuctionService] Anti-sniping kích hoạt: Gia hạn phiên #" + auctionId + " đến " + newEndTime);
        }

        // 7. Lưu lịch sử đặt giá vào bảng bids
        BidTransaction newBid = new BidTransaction();
        newBid.setAuctionId(auctionId);
        newBid.setBidderId(bidderId);
        newBid.setBidAmount(bidAmount);
        newBid.setBidTime(now);

        boolean isBidSaved = bidDAO.addBid(newBid);

        // 8. Cập nhật lại giá cao nhất và người giữ giá cao nhất trong bảng auctions
        if (isBidSaved) {
            boolean updated = auctionDAO.updateHighestBid(auctionId, bidAmount, bidderId);

            // 9. Xử lý cơ chế Auto-bid (Nếu có người đặt giá vượt hạn mức tự động của người khác)
            processAutoBid(auctionId, bidAmount);

            return updated;
        }

        return false;
    }

    // Logic phụ trợ xử lý Auto-bid ngầm
    private void processAutoBid(int auctionId, double currentBid) {
        // Kiểm tra xem phiên này có cấu hình max_auto_bid hay không và kích hoạt tự động đôn giá nếu cần
        double maxAutoBid = auctionDAO.getMaxAutoBid(auctionId);
        int autoBidderId = auctionDAO.getAutoBidderId(auctionId);

        if (maxAutoBid > currentBid && autoBidderId > 0) {
            double nextBid = currentBid + 10.0; // Tăng bước giá mặc định lên 10 đơn vị
            if (nextBid > maxAutoBid) {
                nextBid = maxAutoBid;
            }

            LocalDateTime now = LocalDateTime.now();
            BidTransaction autoBid = new BidTransaction();
            autoBid.setAuctionId(auctionId);
            autoBid.setBidderId(autoBidderId);
            autoBid.setBidAmount(nextBid);
            autoBid.setBidTime(now);

            if (bidDAO.addBid(autoBid)) {
                auctionDAO.updateHighestBid(auctionId, nextBid, autoBidderId);
                System.out.println("[AutoBid] Kích hoạt tự động đặt giá cho User #" + autoBidderId + " với mức: " + nextBid);
            }
        }
    }
}