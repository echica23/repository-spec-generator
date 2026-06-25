package com.example.repository;

import com.example.common.PageRequest;
import com.example.common.PageResult;
import com.example.condition.OrderSearchCondition;
import com.example.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 注文リポジトリ。
 */
public interface OrderRepository {

    /**
     * IDで注文を取得する。
     *
     * @param id 注文ID
     * @return 注文
     */
    Order findById(UUID id);

    /**
     * 注文を保存する。
     *
     * @param order 注文
     * @return 保存後の注文
     */
    Order save(Order order);

    /**
     * 条件に一致する注文を検索する。
     *
     * @param condition 検索条件
     * @param pageRequest ページング要求
     * @return 検索結果
     */
    PageResult<Order> search(OrderSearchCondition condition, PageRequest pageRequest);

    /**
     * 注文をキャンセルする。
     *
     * @param orderId 注文ID
     */
    void cancel(UUID orderId);

    /**
     * 注文の合計金額を計算する。
     *
     * @param orderId 注文ID
     * @return 合計金額
     */
    BigDecimal calculateTotal(UUID orderId);

    /**
     * ユーザーIDで注文一覧を取得する。
     * 将来の拡張試験用: List&lt;Order&gt;（現在のツールでは未対応）
     *
     * @param userId ユーザーID
     * @return 注文一覧
     */
    List<Order> findByUserId(Long userId);

    /**
     * 出荷日時を更新する。
     *
     * @param orderId 注文ID
     * @param shippedAt 出荷日時
     * @return 更新後の注文
     */
    Order updateShippedAt(UUID orderId, OffsetDateTime shippedAt);

    /**
     * 注文統計情報を取得する。
     * 将来の拡張試験用: Map&lt;String, Object&gt;（現在のツールでは未対応）
     *
     * @param condition 検索条件
     * @return 統計情報
     */
    Map<String, Object> getStatistics(OrderSearchCondition condition);

    /**
     * 期間内の注文件数を取得する。
     *
     * @param userId ユーザーID
     * @param orderedFrom 注文日時（開始）
     * @param orderedTo 注文日時（終了）
     * @return 件数
     */
    Integer countByUserIdAndOrderedAtBetween(Long userId, LocalDateTime orderedFrom, LocalDateTime orderedTo);
}
