package org.example.server.dao;

import org.example.model.Auction;
import org.example.server.db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuctionDAO {

    // 1. Tạo phiên đấu giá mới
    public boolean createAuction(Auction auction) {
        String sql = "INSERT INTO auctions (item_id, status, start_time, end_time) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, auction.getItemId());
            pstmt.setString(2, auction.getStatus().name()); // Dùng Enum AuctionStatus
            pstmt.setTimestamp(3, Timestamp.valueOf(auction.getStartTime()));
            pstmt.setTimestamp(4, Timestamp.valueOf(auction.getEndTime()));

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
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Auction auction = new Auction();
                auction.setId(rs.getInt("id"));
                auction.setItemId(rs.getInt("item_id"));
                // Chú ý: Cần viết hàm Getter/Setter trong file Auction.java để không bị báo đỏ
                return auction;
            }
        } catch (SQLException e) {
            System.out.println("[AuctionDAO] Lỗi lấy phiên đấu giá: " + e.getMessage());
        }
        return null;
    }

    // 3. Cập nhật giá cao nhất (Dùng khi có người đặt giá mới)
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
}
