package com.example.condition;

import com.example.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 注文検索条件。
 */
public class OrderSearchCondition {

    /** ユーザーID */
    private Long userId;

    /** 注文番号 */
    private String orderNumber;

    /** 注文ID */
    private UUID orderId;

    /** 注文ステータス */
    private OrderStatus status;

    /** 最低合計金額 */
    private BigDecimal minTotalAmount;

    /** 最高合計金額 */
    private BigDecimal maxTotalAmount;

    /** 注文日時（開始） */
    private LocalDateTime orderedFrom;

    /** 注文日時（終了） */
    private LocalDateTime orderedTo;

    /** 出荷日時（開始） */
    private OffsetDateTime shippedFrom;

    /** ページ番号（0始まり） */
    private Integer page;

    /** 1ページあたりの件数 */
    private Integer size;

    /** ソート項目 */
    private String sortBy;

    /** 支払済みのみ */
    private boolean onlyPaid;

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

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getMinTotalAmount() {
        return minTotalAmount;
    }

    public void setMinTotalAmount(BigDecimal minTotalAmount) {
        this.minTotalAmount = minTotalAmount;
    }

    public BigDecimal getMaxTotalAmount() {
        return maxTotalAmount;
    }

    public void setMaxTotalAmount(BigDecimal maxTotalAmount) {
        this.maxTotalAmount = maxTotalAmount;
    }

    public LocalDateTime getOrderedFrom() {
        return orderedFrom;
    }

    public void setOrderedFrom(LocalDateTime orderedFrom) {
        this.orderedFrom = orderedFrom;
    }

    public LocalDateTime getOrderedTo() {
        return orderedTo;
    }

    public void setOrderedTo(LocalDateTime orderedTo) {
        this.orderedTo = orderedTo;
    }

    public OffsetDateTime getShippedFrom() {
        return shippedFrom;
    }

    public void setShippedFrom(OffsetDateTime shippedFrom) {
        this.shippedFrom = shippedFrom;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public boolean isOnlyPaid() {
        return onlyPaid;
    }

    public void setOnlyPaid(boolean onlyPaid) {
        this.onlyPaid = onlyPaid;
    }
}
