package com.example.entity;

import com.example.common.AuditInfo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * ユーザーエンティティ。
 */
public class User implements com.example.common.Identifiable<Long> {

    /** ユーザーID */
    private Long id;

    /** 公開用UUID */
    private UUID publicId;

    /** 氏名 */
    private String name;

    /** メールアドレス */
    private String email;

    /** 電話番号 */
    private String phone;

    /** 住所 */
    private String address;

    /** 生年月日 */
    private LocalDate birthDate;

    /** 登録日時 */
    private OffsetDateTime registeredAt;

    /** 最終ログイン日時 */
    private LocalDateTime lastLoginAt;

    /** レガシー更新日時 */
    private Date legacyUpdatedAt;

    /** 有効フラグ */
    private boolean active;

    /** 監査情報 */
    private AuditInfo auditInfo;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public void setPublicId(UUID publicId) {
        this.publicId = publicId;
    }

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public OffsetDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(OffsetDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public Date getLegacyUpdatedAt() {
        return legacyUpdatedAt;
    }

    public void setLegacyUpdatedAt(Date legacyUpdatedAt) {
        this.legacyUpdatedAt = legacyUpdatedAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public AuditInfo getAuditInfo() {
        return auditInfo;
    }

    public void setAuditInfo(AuditInfo auditInfo) {
        this.auditInfo = auditInfo;
    }
}
