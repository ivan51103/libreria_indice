package com.biblioteca.repository;

import com.biblioteca.security.User;

public interface UserRepository {
    User findByUsername(String username);
    User save(User user);
}
