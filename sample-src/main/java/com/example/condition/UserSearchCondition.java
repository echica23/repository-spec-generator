package com.example.condition;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ユーザー検索条件。
 */
public class UserSearchCondition {

    /** 氏名（部分一致） */
    private String name;

    /** メールアドレス（部分一致） */
    private String email;

    /** 電話番号 */
    private String phone;

    /** 公開用UUID */
    private UUID publicId;

    /** 生年月日（開始） */
    private LocalDate birthDateFrom;

    /** 生年月日（終了） */
    private LocalDate birthDateTo;

    /** 最終ログイン日時（開始） */
    private LocalDateTime lastLoginFrom;

    /** ページ番号（0始まり） */
    private Integer page;

    /** 1ページあたりの件数 */
    private Integer size;

    /** ソート項目 */
    private String sortBy;

    /** 有効ユーザーのみ */
    private boolean onlyActive;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public void setPublicId(UUID publicId) {
        this.publicId = publicId;
    }

    public LocalDate getBirthDateFrom() {
        return birthDateFrom;
    }

    public void setBirthDateFrom(LocalDate birthDateFrom) {
        this.birthDateFrom = birthDateFrom;
    }

    public LocalDate getBirthDateTo() {
        return birthDateTo;
    }

    public void setBirthDateTo(LocalDate birthDateTo) {
        this.birthDateTo = birthDateTo;
    }

    public LocalDateTime getLastLoginFrom() {
        return lastLoginFrom;
    }

    public void setLastLoginFrom(LocalDateTime lastLoginFrom) {
        this.lastLoginFrom = lastLoginFrom;
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

    public boolean isOnlyActive() {
        return onlyActive;
    }

    public void setOnlyActive(boolean onlyActive) {
        this.onlyActive = onlyActive;
    }
}
