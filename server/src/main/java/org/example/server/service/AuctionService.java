package org.example.server.service;

import org.example.model.Auction;
import org.example.model.BidTransaction;
import org.example.server.dao.AuctionDAO;
import org.example.server.dao.BidDAO;

import java.time.LocalDateTime;

public class AuctionService {
    private final AuctionDAO auctionDAO;
    private final BidDAO bidDAO;

    public AuctionService() {
        this.auctionDAO = new AuctionDAO();
        this.bidDAO = new BidDAO();
    }

    // Từ khóa "synchronized" giúp xử lý đồng thời:
    // Nếu 100 người ấn đặt giá cùng 1 mili-giây, hệ thống sẽ xếp hàng xử lý từng người một, không bị đè giá lộn xộn.
    public synchronized boolean placeBid(int auctionId, int bidderId, double bidAmount) {
        Auction auction = auctionDAO.getAuctionById(auctionId);

        // 1. Kiểm tra phiên đấu giá có tồn tại không
        if (auction == null) {
            System.out.println("[AuctionService] Phiên đấu giá không tồn tại.");
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        // 2. Kiểm tra thời gian
        if (now.isAfter(auction.getEndTime())) {
            System.out.println("[AuctionService] Từ chối: Phiên đấu giá đã kết thúc.");
            return false;
        }

        // 3. Kiểm tra giá đặt có hợp lệ không
        if (bidAmount <= auction.getCurrentHighestBid()) {
            System.out.println("[AuctionService] Từ chối: Giá đặt (" + bidAmount + ") phải cao hơn giá hiện tại (" + auction.getCurrentHighestBid() + ").");
            return false;
        }

        // 4. Tính năng Anti-Sniping (Chống "bơm" giá phút chót)
        // Nếu có người đặt giá trong vòng 5 phút trước khi kết thúc, tự động gia hạn thêm 5 phút
        if (now.plusMinutes(5).isAfter(auction.getEndTime())) {
            LocalDateTime newEndTime = auction.getEndTime().plusMinutes(5);
            // Cập nhật thời gian kết thúc mới vào Database (Ông có thể cần thêm hàm updateEndTime trong AuctionDAO)
            System.out.println("[AuctionService] Anti-sniping kích hoạt: Gia hạn phiên đấu giá " + auctionId + " đến " + newEndTime);
        }

        // 5. Lưu lịch sử đặt giá vào bảng bids
        BidTransaction newBid = new BidTransaction();
        newBid.setAuctionId(auctionId);
        newBid.setBidderId(bidderId);
        newBid.setBidAmount(bidAmount);
        newBid.setBidTime(now);

        boolean isBidSaved = bidDAO.addBid(newBid);

        // 6. Cập nhật lại giá cao nhất trong bảng auctions
        if (isBidSaved) {
            return auctionDAO.updateHighestBid(auctionId, bidAmount, bidderId);
        }

        return false;
    }
}