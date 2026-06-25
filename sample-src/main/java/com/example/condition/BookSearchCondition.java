package com.example.condition;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 書籍検索条件。
 */
public class BookSearchCondition {

    /** タイトル（部分一致） */
    private String title;

    /** 著者名（部分一致） */
    private String author;

    /** ISBN */
    private String isbn;

    /** 最低価格 */
    private BigDecimal minPrice;

    /** 最高価格 */
    private BigDecimal maxPrice;

    /** 出版日（開始） */
    private LocalDate publishedFrom;

    /** 出版日（終了） */
    private LocalDate publishedTo;

    /** ページ番号（0始まり） */
    private Integer page;

    /** 1ページあたりの件数 */
    private Integer size;

    /** ソート項目 */
    private String sortBy;

    /** 在庫ありのみ */
    private boolean onlyAvailable;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public LocalDate getPublishedFrom() {
        return publishedFrom;
    }

    public void setPublishedFrom(LocalDate publishedFrom) {
        this.publishedFrom = publishedFrom;
    }

    public LocalDate getPublishedTo() {
        return publishedTo;
    }

    public void setPublishedTo(LocalDate publishedTo) {
        this.publishedTo = publishedTo;
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

    public boolean isOnlyAvailable() {
        return onlyAvailable;
    }

    public void setOnlyAvailable(boolean onlyAvailable) {
        this.onlyAvailable = onlyAvailable;
    }
}
