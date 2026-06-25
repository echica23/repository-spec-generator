package com.example.common;

import java.util.List;

/**
 * ページング結果を表す共通DTO。
 *
 * @param <T> 要素の型
 */
public class PageResult<T> {

    /** 取得結果 */
    private List<T> content;

    /** 総件数 */
    private Long totalElements;

    /** 総ページ数 */
    private Integer totalPages;

    /** ページング要求 */
    private PageRequest pageRequest;

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public PageRequest getPageRequest() {
        return pageRequest;
    }

    public void setPageRequest(PageRequest pageRequest) {
        this.pageRequest = pageRequest;
    }
}
