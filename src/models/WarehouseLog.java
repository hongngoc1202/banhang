package models;

import java.io.Serializable;
import java.sql.Timestamp;

public class WarehouseLog implements Serializable {
    private int logId;
    private int productId;
    private String logType; // import, export, adjustment
    private int quantity;
    private String reason;
    private int createdBy;
    private Timestamp createdAt;
    private String productName;
    private String createdByName;

    // Constructor
    public WarehouseLog() {}

    public WarehouseLog(int productId, String logType, int quantity, String reason) {
        this.productId = productId;
        this.logType = logType;
        this.quantity = quantity;
        this.reason = reason;
    }

    // Getters and Setters
    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }
}
