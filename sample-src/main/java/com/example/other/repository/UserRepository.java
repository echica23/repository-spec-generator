package com.example.other.repository;

import com.example.entity.User;

/**
 * 別パッケージの UserRepository（同名 Repository 検証用）。
 */
public interface UserRepository {

    User findById(Long id);
}
