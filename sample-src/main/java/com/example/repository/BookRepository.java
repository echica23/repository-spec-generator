package com.example.repository;

import com.example.common.PageRequest;
import com.example.common.PageResult;
import com.example.condition.BookSearchCondition;
import com.example.entity.Book;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 書籍リポジトリ。
 */
public interface BookRepository {

    /**
     * IDで書籍を取得する。
     *
     * @param id 書籍ID
     * @return 書籍
     */
    Book findById(Long id);

    /**
     * 書籍を保存する。
     *
     * @param book 書籍
     * @return 保存後の書籍
     */
    Book save(Book book);

    /**
     * 条件に一致する書籍を検索する。
     *
     * @param condition 検索条件
     * @param pageRequest ページング要求
     * @return 検索結果
     */
    PageResult<Book> search(BookSearchCondition condition, PageRequest pageRequest);

    /**
     * 書籍を削除する。
     *
     * @param id 書籍ID
     */
    void deleteById(Long id);

    /**
     * 著者名に一致する書籍件数を取得する。
     *
     * @param author 著者名
     * @return 件数
     */
    int countByAuthor(String author);

    /**
     * IDで書籍を取得する（Optional版）。
     * 将来の拡張試験用: Optional&lt;Book&gt;（現在のツールでは未対応）
     *
     * @param id 書籍ID
     * @return 書籍
     */
    Optional<Book> findOptionalById(Long id);

    /**
     * 出版日で書籍一覧を取得する。
     * 将来の拡張試験用: List&lt;Book&gt;（現在のツールでは未対応）
     *
     * @param publishedDate 出版日
     * @return 書籍一覧
     */
    List<Book> findByPublishedDate(LocalDate publishedDate);

    /**
     * 複数IDで書籍配列を取得する。
     * 将来の拡張試験用: Book[]（現在のツールでは未対応）
     *
     * @param ids 書籍ID配列
     * @return 書籍配列
     */
    Book[] findByIds(Long[] ids);
}
