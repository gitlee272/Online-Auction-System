package org.example.server.dao;

import org.example.model.BidTransaction;
import org.example.server.db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BidDAO {

    // 1. Lưu thông tin mỗi lần người dùng bấm đặt giá
    public boolean addBid(BidTransaction bid) {
        String sql = "INSERT INTO bids (auction_id, bidder_id, bid_amount, bid_time) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bid.getAuctionId());
            pstmt.setInt(2, bid.getBidderId());
            pstmt.setDouble(3, bid.getBidAmount());
            pstmt.setTimestamp(4, Timestamp.valueOf(bid.getBidTime()));

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[BidDAO] Lỗi lưu lịch sử đặt giá: " + e.getMessage());
            return false;
        }
    }

    // 2. Lấy toàn bộ lịch sử đặt giá của một phiên đấu giá (sắp xếp giá từ cao xuống thấp)
    public List<BidTransaction> getBidsByAuctionId(int auctionId) {
        List<BidTransaction> bids = new ArrayList<>();
        String sql = "SELECT * FROM bids WHERE auction_id = ? ORDER BY bid_amount DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, auctionId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                BidTransaction bid = new BidTransaction();
                bid.setId(rs.getInt("id"));
                bid.setAuctionId(rs.getInt("auction_id"));
                bid.setBidderId(rs.getInt("bidder_id"));
                bid.setBidAmount(rs.getDouble("bid_amount"));
                bid.setBidTime(rs.getTimestamp("bid_time").toLocalDateTime());
                bids.add(bid);
            }
        } catch (SQLException e) {
            System.out.println("[BidDAO] Lỗi lấy danh sách đặt giá: " + e.getMessage());
        }
        return bids;
    }
}