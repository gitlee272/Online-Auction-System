package org.example.server.db;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void initialize() {
        // Lệnh tạo bảng Sản phẩm (Item)
        String createItemTable = "CREATE TABLE IF NOT EXISTS items ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "description TEXT, "
                + "starting_price REAL, "
                + "condition TEXT"
                + ");";

        // Lệnh tạo bảng Phiên đấu giá (Auction)
        String createAuctionTable = "CREATE TABLE IF NOT EXISTS auctions ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "item_id INTEGER, "
                + "starting_price REAL, "
                + "current_highest_bid REAL, "
                + "start_time TEXT, "
                + "end_time TEXT, "
                + "status TEXT, "
                + "winner_id INTEGER, "
                + "FOREIGN KEY(item_id) REFERENCES items(id)"
                + ");";

        // Lệnh tạo bảng Lịch sử trả giá (BidTransaction)
        String createBidTable = "CREATE TABLE IF NOT EXISTS bid_transactions ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "auction_id INTEGER, "
                + "bidder_id INTEGER, "
                + "bid_amount REAL, "
                + "bid_time TEXT, "
                + "FOREIGN KEY(auction_id) REFERENCES auctions(id)"
                + ");";

        // Thực thi tạo bảng
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createItemTable);
            stmt.execute(createAuctionTable);
            stmt.execute(createBidTable);
            System.out.println("[Database] Khoi tao co so du lieu SQLite thanh cong!");
        } catch (Exception e) {
            System.out.println("[Database] Loi khoi tao: " + e.getMessage());
        }
    }
}
