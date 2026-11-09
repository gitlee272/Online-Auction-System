package org.example.server.dao;

import org.example.enums.ItemCondition;
import org.example.model.Art;
import org.example.model.Electronics;
import org.example.model.Item;
import org.example.model.ItemFactory;
import org.example.model.Vehicle;
import org.example.server.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    public int createItem(Item item, String category) {
        String sql = "INSERT INTO items " +
                "(name, description, starting_price, condition, " +
                "category, seller_id, extra_info_1, extra_info_2) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        String extra1 = "";
        double extra2 = 0.0;
        String finalCategory = category != null ? category : "GENERAL";

        if (item instanceof Electronics) {
            Electronics e = (Electronics) item;
            extra1 = e.getBrand() != null ? e.getBrand() : "";
            extra2 = e.getWarrantyMonths();
            finalCategory = "ELECTRONICS";
        } else if (item instanceof Art) {
            Art a = (Art) item;
            extra1 = a.getArtist() != null ? a.getArtist() : "";
            extra2 = a.getCreationYear();
            finalCategory = "ART";
        } else if (item instanceof Vehicle) {
            Vehicle v = (Vehicle) item;
            extra1 = v.getModelName() != null ? v.getModelName() : "";
            extra2 = v.getMileage();
            finalCategory = "VEHICLE";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, item.getName());
            pstmt.setString(2, item.getDescription());
            pstmt.setDouble(3, item.getStartingPrice());
            String cond = item.getCondition() != null
                    ? item.getCondition().name() : "NEW";
            pstmt.setString(4, cond);
            pstmt.setString(5, finalCategory);
            pstmt.setInt(6, item.getSellerId());
            pstmt.setString(7, extra1);
            pstmt.setDouble(8, extra2);

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        item.setId(generatedId);
                        return generatedId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Loi khi tao Item: " + e.getMessage());
        }
        return -1;
    }

    public boolean addItem(Item item, String category) {
        return createItem(item, category) > 0;
    }

    public boolean addItem(Item item) {
        return createItem(item, "GENERAL") > 0;
    }

    public Item getItemById(int id) {
        String sql = "SELECT * FROM items WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToItem(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Loi khi tim Item: " + e.getMessage());
        }
        return null;
    }

    public List<Item> getAllItems() {
        List<Item> list = new ArrayList<Item>();
        String sql = "SELECT * FROM items";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRowToItem(rs));
            }
        } catch (SQLException e) {
            System.err.println("Loi lay danh sach Item: " + e.getMessage());
        }
        return list;
    }

    public boolean deleteItem(int id) {
        String sql = "DELETE FROM items WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Loi xoa Item: " + e.getMessage());
            return false;
        }
    }

    private Item mapRowToItem(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        double startingPrice = rs.getDouble("starting_price");
        String condStr = rs.getString("condition");
        String category = rs.getString("category");
        int sellerId = rs.getInt("seller_id");

        String extra1 = "";
        double extra2 = 0.0;
        try {
            extra1 = rs.getString("extra_info_1");
            extra2 = rs.getDouble("extra_info_2");
        } catch (SQLException ignored) {
        }

        ItemCondition condition = ItemCondition.NEW;
        if (condStr != null) {
            try {
                condition = ItemCondition.valueOf(condStr.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        return ItemFactory.createItem(
                category,
                id,
                name,
                description,
                startingPrice,
                condition,
                sellerId,
                extra1,
                extra2
        );
    }
}

