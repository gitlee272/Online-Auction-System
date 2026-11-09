package org.example.server.dao;

import org.example.model.Auction;
import org.example.enums.AuctionStatus;
import org.example.server.db.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;

public class AuctionDAO {

    // 1. Tạo phiên đấu giá mới
    public boolean createAuction(Auction auction) {
        String sql = "INSERT INTO auctions (item_id, status, start_time, end_time, current_highest_bid, highest_bidder_id, max_auto_bid) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, auction.getItemId());
            pstmt.setString(2, auction.getStatus() != null ? auction.getStatus().name() : AuctionStatus.ACTIVE.name());
            pstmt.setTimestamp(3, Timestamp.valueOf(auction.getStartTime()));
            pstmt.setTimestamp(4, Timestamp.valueOf(auction.getEndTime()));
            pstmt.setDouble(5, 0.0); // Giá khởi điểm ban đầu
            pstmt.setInt(6, 0);
            pstmt.setDouble(7, 0.0);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[AuctionDAO] Lỗi tạo phiên đấu giá: " + e.getMessage());
            return false;
        }
    }

    // 2. Lấy thông tin phiên đấu giá theo ID
    public Auction getAuctionById(int id) {
        String sql = "SELECT * FROM auctions WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Auction auction = new Auction();
                    auction.setId(rs.getInt("id"));
                    auction.setItemId(rs.getInt("item_id"));
                    auction.setCurrentHighestBid(rs.getDouble("current_highest_bid"));

                    Timestamp startTs = rs.getTimestamp("start_time");
                    if (startTs != null) {
                        auction.setStartTime(startTs.toLocalDateTime());
                    }

                    Timestamp endTs = rs.getTimestamp("end_time");
                    if (endTs != null) {
                        auction.setEndTime(endTs.toLocalDateTime());
                    }

                    String statusStr = rs.getString("status");
                    if (statusStr != null) {
                        try {
                            auction.setStatus(AuctionStatus.valueOf(statusStr));
                        } catch (IllegalArgumentException ignored) {}
                    }

                    return auction;
                }
            }
        } catch (SQLException e) {
            System.out.println("[AuctionDAO] Lỗi lấy phiên đấu giá: " + e.getMessage());
        }
        return null;
    }

    // 3. Cập nhật giá cao nhất
    public boolean updateHighestBid(int auctionId, double newBid, int bidderId) {
        String sql = "UPDATE auctions SET current_highest_bid = ?, highest_bidder_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, newBid);
            pstmt.setInt(2, bidderId);
            pstmt.setInt(3, auctionId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[AuctionDAO] Lỗi cập nhật giá: " + e.getMessage());
            return false;
        }
    }

    // 4. Cập nhật thời gian kết thúc (Anti-sniping)
    public boolean updateAuctionEndTime(int auctionId, LocalDateTime newEndTime) {
        String sql = "UPDATE auctions SET end_time = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(newEndTime));
            pstmt.setInt(2, auctionId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[AuctionDAO] Lỗi cập nhật thời gian kết thúc: " + e.getMessage());
            return false;
        }
    }

    // 5. Lấy hạn mức Auto-bid tối đa
    public double getMaxAutoBid(int auctionId) {
        String sql = "SELECT max_auto_bid FROM auctions WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, auctionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("max_auto_bid");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy max_auto_bid: " + e.getMessage());
        }
        return 0.0;
    }

    // 6. Lấy ID người dùng bật Auto-bid
    public int getAutoBidderId(int auctionId) {
        String sql = "SELECT highest_bidder_id FROM auctions WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, auctionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("highest_bidder_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy autoBidderId: " + e.getMessage());
        }
        return 0;
    }
}