package dao;

import models.CartItem;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {
    
    public boolean addToCart(CartItem item) {
        String query = "INSERT INTO carts (user_id, product_id, quantity) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE quantity = quantity + ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, item.getUserId());
            pstmt.setInt(2, item.getProductId());
            pstmt.setInt(3, item.getQuantity());
            pstmt.setInt(4, item.getQuantity());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<CartItem> getCartByUserId(int userId) {
        List<CartItem> cartItems = new ArrayList<>();
        String query = "SELECT c.cart_id, c.user_id, c.product_id, c.quantity, c.added_at, p.product_name, p.price, p.image_url " +
                      "FROM carts c JOIN products p ON c.product_id = p.product_id WHERE c.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                cartItems.add(mapResultSetToCartItem(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cartItems;
    }
    
    public boolean updateCartItem(int cartId, int quantity) {
        String query = "UPDATE carts SET quantity = ? WHERE cart_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, cartId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean removeCartItem(int cartId) {
        String query = "DELETE FROM carts WHERE cart_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, cartId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean removeCartItemByUserAndProduct(int userId, int productId) {
        String query = "DELETE FROM carts WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, productId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean clearCart(int userId) {
        String query = "DELETE FROM carts WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public int getCartItemCount(int userId) {
        String query = "SELECT COUNT(*) FROM carts WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private CartItem mapResultSetToCartItem(ResultSet rs) throws SQLException {
        CartItem item = new CartItem();
        item.setCartId(rs.getInt("cart_id"));
        item.setUserId(rs.getInt("user_id"));
        item.setProductId(rs.getInt("product_id"));
        item.setQuantity(rs.getInt("quantity"));
        item.setAddedAt(rs.getTimestamp("added_at"));
        item.setProductName(rs.getString("product_name"));
        item.setPrice(rs.getDouble("price"));
        item.setImageUrl(rs.getString("image_url"));
        return item;
    }
}
