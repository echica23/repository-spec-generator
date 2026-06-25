package com.example.common;

/**
 * 識別子を持つオブジェクトを表す共通インターフェース。
 * 将来の拡張試験用（現在のツールでは未対応）。
 *
 * @param <T> 識別子の型
 */
public interface Identifiable<T> {

    /**
     * 識別子を取得する。
     *
     * @return 識別子
     */
    T getId();
}
