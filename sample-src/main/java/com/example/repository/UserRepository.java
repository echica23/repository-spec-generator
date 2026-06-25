package com.example.repository;

import com.example.common.PageRequest;
import com.example.common.PageResult;
import com.example.condition.UserSearchCondition;
import com.example.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * ユーザーリポジトリ。
 */
public interface UserRepository {

    /**
     * IDでユーザーを取得する。
     *
     * @param id ユーザーID
     * @return ユーザー
     */
    User findById(Long id);

    /**
     * ユーザーを保存する。
     *
     * @param user ユーザー
     * @return 保存後のユーザー
     */
    User save(User user);

    /**
     * 条件に一致するユーザーを検索する。
     *
     * @param condition 検索条件
     * @param pageRequest ページング要求
     * @return 検索結果
     */
    PageResult<User> search(UserSearchCondition condition, PageRequest pageRequest);

    /**
     * メールアドレスでユーザーを取得する。
     * 将来の拡張試験用: Optional&lt;User&gt;（現在のツールでは未対応）
     *
     * @param email メールアドレス
     * @return ユーザー
     */
    Optional<User> findByEmail(String email);

    /**
     * 公開用UUIDでユーザーの存在を確認する。
     *
     * @param publicId 公開用UUID
     * @return 存在する場合true
     */
    boolean existsByPublicId(UUID publicId);

    /**
     * 最終ログイン日時を更新する。
     *
     * @param id ユーザーID
     * @param lastLoginAt 最終ログイン日時
     */
    void updateLastLogin(Long id, LocalDateTime lastLoginAt);

    /**
     * 期限切れユーザーを削除する。
     *
     * @param expiredBefore 期限日
     * @return 削除件数
     */
    int deleteExpiredUsers(LocalDate expiredBefore);

    /**
     * レガシー更新日時を取得する。
     *
     * @param id ユーザーID
     * @return レガシー更新日時
     */
    Date findLegacyUpdatedAt(Long id);

    /**
     * 登録日時の範囲でユーザー一覧を取得する。
     * 将来の拡張試験用: List&lt;User&gt;（現在のツールでは未対応）
     *
     * @param registeredFrom 登録日時（開始）
     * @param registeredTo 登録日時（終了）
     * @return ユーザー一覧
     */
    List<User> findByRegisteredAtBetween(OffsetDateTime registeredFrom, OffsetDateTime registeredTo);
}
