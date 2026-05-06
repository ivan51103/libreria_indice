package com.biblioteca.repository.memory;

import com.biblioteca.repository.UserRepository;
import com.biblioteca.security.User;
import com.biblioteca.security.UserRole;
import java.util.LinkedHashMap;
import java.util.Map;

public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> storage = new LinkedHashMap<>();

    public InMemoryUserRepository() {
        User admin = new User();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setPasswordHash("admin123");
        admin.setRole(UserRole.ADMIN);
        storage.put(admin.getUsername(), admin);
    }

    @Override
    public User findByUsername(String username) {
        return storage.get(username);
    }

    @Override
    public User save(User user) {
        storage.put(user.getUsername(), user);
        return user;
    }
}
