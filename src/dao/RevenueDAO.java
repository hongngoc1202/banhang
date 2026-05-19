package dao;

import models.Revenue;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RevenueDAO {
    
    public boolean addRevenue(Revenue revenue) {
        String query = "INSERT INTO revenue (order_id, amount, revenue_date) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, revenue.getOrderId());
            pstmt.setDouble(2, revenue.getAmount());
            pstmt.setDate(3, new java.sql.Date(revenue.getRevenueDate().getTime()));
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public double getTotalRevenueByDate(java.sql.Date date) {
        String query = "SELECT SUM(amount) as total FROM revenue WHERE revenue_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDate(1, date);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                double total = rs.getDouble("total");
                return total > 0 ? total : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public double getTotalRevenueByMonth(int month, int year) {
        String query = "SELECT SUM(amount) as total FROM revenue WHERE MONTH(revenue_date) = ? AND YEAR(revenue_date) = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, month);
            pstmt.setInt(2, year);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                double total = rs.getDouble("total");
                return total > 0 ? total : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public double getTotalRevenueByYear(int year) {
        String query = "SELECT SUM(amount) as total FROM revenue WHERE YEAR(revenue_date) = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, year);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                double total = rs.getDouble("total");
                return total > 0 ? total : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public List<Revenue> getRevenueByMonth(int month, int year) {
        List<Revenue> revenues = new ArrayList<>();
        String query = "SELECT * FROM revenue WHERE MONTH(revenue_date) = ? AND YEAR(revenue_date) = ? ORDER BY revenue_date";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, month);
            pstmt.setInt(2, year);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                revenues.add(mapResultSetToRevenue(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return revenues;
    }
    
    public List<Object[]> getDailyRevenueByMonth(int month, int year) {
        List<Object[]> dailyRevenue = new ArrayList<>();
        String query = "SELECT DAY(revenue_date) as day, SUM(amount) as total FROM revenue " +
                      "WHERE MONTH(revenue_date) = ? AND YEAR(revenue_date) = ? " +
                      "GROUP BY DAY(revenue_date) ORDER BY DAY(revenue_date)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, month);
            pstmt.setInt(2, year);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Object[] data = new Object[2];
                data[0] = rs.getInt("day");
                data[1] = rs.getDouble("total");
                dailyRevenue.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dailyRevenue;
    }
    
    public List<Object[]> getMonthlyRevenueByYear(int year) {
        List<Object[]> monthlyRevenue = new ArrayList<>();
        String query = "SELECT MONTH(revenue_date) as month, SUM(amount) as total FROM revenue " +
                      "WHERE YEAR(revenue_date) = ? " +
                      "GROUP BY MONTH(revenue_date) ORDER BY MONTH(revenue_date)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, year);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Object[] data = new Object[2];
                data[0] = rs.getInt("month");
                data[1] = rs.getDouble("total");
                monthlyRevenue.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return monthlyRevenue;
    }
    
    private Revenue mapResultSetToRevenue(ResultSet rs) throws SQLException {
        Revenue revenue = new Revenue();
        revenue.setRevenueId(rs.getInt("revenue_id"));
        revenue.setOrderId(rs.getInt("order_id"));
        revenue.setAmount(rs.getDouble("amount"));
        revenue.setRevenueDate(rs.getDate("revenue_date"));
        return revenue;
    }
}
