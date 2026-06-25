package com.example.entity;

import java.math.BigDecimal;

/**
 * 注文明細を表すネストDTO。
 * 将来の拡張試験用（現在のツールでは未対応）。
 */
public class OrderItem {

    /** 書籍ID */
    private Long bookId;

    /** 書籍タイトル */
    private String bookTitle;

    /** 数量 */
    private Integer quantity;

    /** 単価 */
    private BigDecimal unitPrice;

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
