package dao;

import models.OrderDetail;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailDAO {
    
    public boolean addOrderDetail(OrderDetail detail) {
        String query = "INSERT INTO order_details (order_id, product_id, quantity, unit_price, total_price) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, detail.getOrderId());
            pstmt.setInt(2, detail.getProductId());
            pstmt.setInt(3, detail.getQuantity());
            pstmt.setDouble(4, detail.getUnitPrice());
            pstmt.setDouble(5, detail.getTotalPrice());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {
        List<OrderDetail> details = new ArrayList<>();
        String query = "SELECT od.*, p.product_name, p.image_url FROM order_details od JOIN products p ON od.product_id = p.product_id WHERE od.order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                details.add(mapResultSetToOrderDetail(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }
    
    private OrderDetail mapResultSetToOrderDetail(ResultSet rs) throws SQLException {
        OrderDetail detail = new OrderDetail();
        detail.setOrderDetailId(rs.getInt("order_detail_id"));
        detail.setOrderId(rs.getInt("order_id"));
        detail.setProductId(rs.getInt("product_id"));
        detail.setQuantity(rs.getInt("quantity"));
        detail.setUnitPrice(rs.getDouble("unit_price"));
        detail.setTotalPrice(rs.getDouble("total_price"));
        detail.setProductName(rs.getString("product_name"));
        detail.setImageUrl(rs.getString("image_url"));
        return detail;
    }
}
