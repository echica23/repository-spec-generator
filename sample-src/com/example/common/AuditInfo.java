package com.example.common;

import java.time.LocalDateTime;

/**
 * 監査情報を表す共通DTO。
 */
public class AuditInfo {

    /** 作成者 */
    private String createdBy;

    /** 作成日時 */
    private LocalDateTime createdAt;

    /** 更新者 */
    private String updatedBy;

    /** 更新日時 */
    private LocalDateTime updatedAt;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
