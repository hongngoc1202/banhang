package models;

import java.io.Serializable;
import java.sql.Date;

public class Revenue implements Serializable {
    private int revenueId;
    private int orderId;
    private double amount;
    private Date revenueDate;

    // Constructor
    public Revenue() {}

    public Revenue(int orderId, double amount, Date revenueDate) {
        this.orderId = orderId;
        this.amount = amount;
        this.revenueDate = revenueDate;
    }

    // Getters and Setters
    public int getRevenueId() {
        return revenueId;
    }

    public void setRevenueId(int revenueId) {
        this.revenueId = revenueId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getRevenueDate() {
        return revenueDate;
    }

    public void setRevenueDate(Date revenueDate) {
        this.revenueDate = revenueDate;
    }
}
