package com.example.common;

/**
 * ページング要求を表す共通DTO。
 */
public class PageRequest {

    /** ページ番号（0始まり） */
    private Integer page;

    /** 1ページあたりの件数 */
    private Integer size;

    /** ソート項目 */
    private String sortBy;

    /** ソート方向 */
    private String sortDirection;

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

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }
}
