package com.example.entity;

import com.example.common.AuditInfo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 注文エンティティ。
 */
public class Order implements com.example.common.Identifiable<UUID> {

    /** 注文ID */
    private UUID id;

    /** ユーザーID */
    private Long userId;

    /** 注文番号 */
    private String orderNumber;

    /** 合計金額 */
    private BigDecimal totalAmount;

    /** 注文日時 */
    private LocalDateTime orderedAt;

    /** 出荷日時 */
    private OffsetDateTime shippedAt;

    /** 配送先住所 */
    private String shippingAddress;

    /** 注文ステータス */
    private OrderStatus status;

    /** 明細件数 */
    private int itemCount;

    /** 支払済みフラグ */
    private boolean paid;

    /** 注文明細一覧 */
    private List<OrderItem> items;

    /** 監査情報 */
    private AuditInfo auditInfo;

    @Override
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }

    public void setOrderedAt(LocalDateTime orderedAt) {
        this.orderedAt = orderedAt;
    }

    public OffsetDateTime getShippedAt() {
        return shippedAt;
    }

    public void setShippedAt(OffsetDateTime shippedAt) {
        this.shippedAt = shippedAt;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public AuditInfo getAuditInfo() {
        return auditInfo;
    }

    public void setAuditInfo(AuditInfo auditInfo) {
        this.auditInfo = auditInfo;
    }
}
