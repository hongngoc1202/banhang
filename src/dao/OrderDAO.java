package dao;

import models.Order;
import models.OrderDetail;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    
    public int addOrder(Order order) {
        String query = "INSERT INTO orders (user_id, total_amount, status, payment_method, shipping_address, notes) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, order.getUserId());
            pstmt.setDouble(2, order.getTotalAmount());
            pstmt.setString(3, order.getStatus() != null ? order.getStatus() : "pending");
            pstmt.setString(4, order.getPaymentMethod());
            pstmt.setString(5, order.getShippingAddress());
            pstmt.setString(6, order.getNotes());
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public Order getOrderById(int orderId) {
        String query = "SELECT o.*, u.full_name, u.phone FROM orders o JOIN users u ON o.user_id = u.user_id WHERE o.order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToOrder(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT o.*, u.full_name, u.phone FROM orders o JOIN users u ON o.user_id = u.user_id WHERE o.user_id = ? ORDER BY o.order_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT o.*, u.full_name, u.phone FROM orders o JOIN users u ON o.user_id = u.user_id ORDER BY o.order_date DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    
    public boolean updateOrderStatus(int orderId, String status) {
        String query = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteOrder(int orderId) {
        String query = "DELETE FROM orders WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, orderId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));
        order.setUserId(rs.getInt("user_id"));
        order.setOrderDate(rs.getTimestamp("order_date"));
        order.setTotalAmount(rs.getDouble("total_amount"));
        order.setStatus(rs.getString("status"));
        order.setPaymentMethod(rs.getString("payment_method"));
        order.setShippingAddress(rs.getString("shipping_address"));
        order.setNotes(rs.getString("notes"));
        order.setUserName(rs.getString("full_name"));
        order.setUserPhone(rs.getString("phone"));
        return order;
    }
}
