package dao;

import models.Category;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    
    public boolean addCategory(Category category) {
        String query = "INSERT INTO categories (category_name, description) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, category.getCategoryName());
            pstmt.setString(2, category.getDescription());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public Category getCategoryById(int categoryId) {
        String query = "SELECT * FROM categories WHERE category_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, categoryId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToCategory(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT * FROM categories ORDER BY category_name";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
    
    public boolean updateCategory(Category category) {
        String query = "UPDATE categories SET category_name = ?, description = ? WHERE category_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, category.getCategoryName());
            pstmt.setString(2, category.getDescription());
            pstmt.setInt(3, category.getCategoryId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteCategory(int categoryId) {
        String query = "DELETE FROM categories WHERE category_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, categoryId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getInt("category_id"));
        category.setCategoryName(rs.getString("category_name"));
        category.setDescription(rs.getString("description"));
        category.setCreatedAt(rs.getTimestamp("created_at"));
        return category;
    }
}
