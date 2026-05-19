package dao;

import models.Product;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    
    public boolean addProduct(Product product) {
        String query = "INSERT INTO products (category_id, product_name, description, price, stock_quantity, image_url, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, product.getCategoryId());
            pstmt.setString(2, product.getProductName());
            pstmt.setString(3, product.getDescription());
            pstmt.setDouble(4, product.getPrice());
            pstmt.setInt(5, product.getStockQuantity());
            pstmt.setString(6, product.getImageUrl());
            pstmt.setString(7, product.getStatus() != null ? product.getStatus() : "active");
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public Product getProductById(int productId) {
        String query = "SELECT * FROM products WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToProduct(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products ORDER BY product_name";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    
    public List<Product> getProductsByCategory(int categoryId) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products WHERE category_id = ? AND status = 'active' ORDER BY product_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, categoryId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    
    public List<Product> searchProducts(String keyword) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products WHERE (product_name LIKE ? OR description LIKE ?) AND status = 'active' ORDER BY product_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            String searchKeyword = "%" + keyword + "%";
            pstmt.setString(1, searchKeyword);
            pstmt.setString(2, searchKeyword);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    
    public boolean updateProduct(Product product) {
        String query = "UPDATE products SET category_id = ?, product_name = ?, description = ?, price = ?, stock_quantity = ?, image_url = ?, status = ? WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, product.getCategoryId());
            pstmt.setString(2, product.getProductName());
            pstmt.setString(3, product.getDescription());
            pstmt.setDouble(4, product.getPrice());
            pstmt.setInt(5, product.getStockQuantity());
            pstmt.setString(6, product.getImageUrl());
            pstmt.setString(7, product.getStatus());
            pstmt.setInt(8, product.getProductId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteProduct(int productId) {
        String query = "DELETE FROM products WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, productId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<Product> getProductsLowStock(int threshold) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products WHERE stock_quantity <= ? ORDER BY stock_quantity";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, threshold);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setCategoryId(rs.getInt("category_id"));
        product.setProductName(rs.getString("product_name"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getDouble("price"));
        product.setStockQuantity(rs.getInt("stock_quantity"));
        product.setImageUrl(rs.getString("image_url"));
        product.setStatus(rs.getString("status"));
        product.setCreatedAt(rs.getTimestamp("created_at"));
        product.setUpdatedAt(rs.getTimestamp("updated_at"));
        return product;
    }
}
