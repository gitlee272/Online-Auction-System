package org.example.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Tên file CSDL sẽ tự động được tạo ra trong thư mục gốc của dự án
    private static final String URL = "jdbc:sqlite:auction_system.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
