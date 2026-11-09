package org.example.server.dao;
import org.example.model.Item;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// Đã import kết nối Database của ông
import org.example.server.db.DatabaseConnection;

public class ItemDAO {

    // 1. Thêm một sản phẩm mới vào cơ sở dữ liệu
    public boolean addItem(Item item) {
        String sql = "INSERT INTO items (name, description, starting_price, condition) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item.getName());
            pstmt.setString(2, item.getDescription());
            pstmt.setDouble(3, item.getStartingPrice());
            pstmt.setString(4, item.getCondition().name());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("[ItemDAO] Lỗi thêm sản phẩm: " + e.getMessage());
            return false;
        }
    }

    // 2. Lấy danh sách toàn bộ sản phẩm đang có
    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Item item = new Item();
                item.setId(rs.getInt("id"));
                item.setName(rs.getString("name"));
                item.setDescription(rs.getString("description"));
                item.setStartingPrice(rs.getDouble("starting_price"));
                // item.setCondition(ItemCondition.valueOf(rs.getString("condition")));

                items.add(item);
            }
        } catch (SQLException e) {
            System.out.println("[ItemDAO] Lỗi lấy danh sách sản phẩm: " + e.getMessage());
        }
        return items;
    }
    // 3. Cập nhật thông tin sản phẩm
    public boolean updateItem(Item item) {
        String sql = "UPDATE items SET name = ?, description = ?, starting_price = ?, condition = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item.getName());
            pstmt.setString(2, item.getDescription());
            pstmt.setDouble(3, item.getStartingPrice());
            pstmt.setString(4, item.getCondition().name());
            pstmt.setInt(5, item.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("[ItemDAO] Lỗi cập nhật sản phẩm: " + e.getMessage());
            return false;
        }
    }

    // 4. Xóa sản phẩm theo ID
    public boolean deleteItem(int id) {
        String sql = "DELETE FROM items WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("[ItemDAO] Lỗi xóa sản phẩm: " + e.getMessage());
            return false;
        }
    }
}
