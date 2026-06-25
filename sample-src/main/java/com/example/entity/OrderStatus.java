package com.example.entity;

/**
 * 注文ステータスを表す列挙型。
 * 将来の拡張試験用（現在のツールでは未対応）。
 */
public enum OrderStatus {

    /** 受付 */
    RECEIVED,

    /** 処理中 */
    PROCESSING,

    /** 出荷済み */
    SHIPPED,

    /** 完了 */
    COMPLETED,

    /** キャンセル */
    CANCELLED
}
