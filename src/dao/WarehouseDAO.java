package dao;

import models.WarehouseLog;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarehouseDAO {
    
    public int getStockQuantity(int productId) {
        String query = "SELECT quantity FROM warehouse WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("quantity");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public boolean addStockLog(WarehouseLog log) {
        String query = "INSERT INTO warehouse_logs (product_id, log_type, quantity, reason, created_by) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, log.getProductId());
            pstmt.setString(2, log.getLogType());
            pstmt.setInt(3, log.getQuantity());
            pstmt.setString(4, log.getReason());
            pstmt.setInt(5, log.getCreatedBy());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean importStock(int productId, int quantity, int userId) {
        String query = "UPDATE warehouse SET quantity = quantity + ? WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, productId);
            pstmt.executeUpdate();
            
            // Add log
            WarehouseLog log = new WarehouseLog();
            log.setProductId(productId);
            log.setLogType("import");
            log.setQuantity(quantity);
            log.setReason("Nhập hàng");
            log.setCreatedBy(userId);
            addStockLog(log);
            
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean exportStock(int productId, int quantity, int userId) {
        String query = "UPDATE warehouse SET quantity = quantity - ? WHERE product_id = ? AND quantity >= ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, productId);
            pstmt.setInt(3, quantity);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Add log
                WarehouseLog log = new WarehouseLog();
                log.setProductId(productId);
                log.setLogType("export");
                log.setQuantity(quantity);
                log.setReason("Xuất hàng");
                log.setCreatedBy(userId);
                addStockLog(log);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<WarehouseLog> getAllWarehouseLogs() {
        List<WarehouseLog> logs = new ArrayList<>();
        String query = "SELECT wl.*, p.product_name, u.full_name FROM warehouse_logs wl " +
                      "JOIN products p ON wl.product_id = p.product_id " +
                      "JOIN users u ON wl.created_by = u.user_id " +
                      "ORDER BY wl.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                logs.add(mapResultSetToWarehouseLog(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }
    
    public List<WarehouseLog> getWarehouseLogsByProductId(int productId) {
        List<WarehouseLog> logs = new ArrayList<>();
        String query = "SELECT wl.*, p.product_name, u.full_name FROM warehouse_logs wl " +
                      "JOIN products p ON wl.product_id = p.product_id " +
                      "JOIN users u ON wl.created_by = u.user_id " +
                      "WHERE wl.product_id = ? ORDER BY wl.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                logs.add(mapResultSetToWarehouseLog(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }
    
    private WarehouseLog mapResultSetToWarehouseLog(ResultSet rs) throws SQLException {
        WarehouseLog log = new WarehouseLog();
        log.setLogId(rs.getInt("log_id"));
        log.setProductId(rs.getInt("product_id"));
        log.setLogType(rs.getString("log_type"));
        log.setQuantity(rs.getInt("quantity"));
        log.setReason(rs.getString("reason"));
        log.setCreatedBy(rs.getInt("created_by"));
        log.setCreatedAt(rs.getTimestamp("created_at"));
        log.setProductName(rs.getString("product_name"));
        log.setCreatedByName(rs.getString("full_name"));
        return log;
    }
}
