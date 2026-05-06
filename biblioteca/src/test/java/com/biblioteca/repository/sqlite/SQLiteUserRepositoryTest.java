package com.biblioteca.repository.sqlite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.biblioteca.config.ConnectionProvider;
import com.biblioteca.config.DatabaseInitializer;
import com.biblioteca.security.User;
import com.biblioteca.security.UserRole;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SQLiteUserRepositoryTest {
    @TempDir
    Path tempDir;

    private SQLiteUserRepository userRepository;

    @BeforeEach
    void setUp() {
        ConnectionProvider connectionProvider = new TestConnectionProvider(tempDir.resolve("users-test.db"));
        new DatabaseInitializer(connectionProvider).initialize();
        userRepository = new SQLiteUserRepository(connectionProvider);
    }

    @Test
    void shouldSaveAndFindUserByUsername() {
        User user = user("admin", "pbkdf2-hash", UserRole.ADMIN);

        userRepository.save(user);
        User stored = userRepository.findByUsername("admin");

        assertNotNull(user.getId());
        assertNotNull(stored);
        assertEquals(user.getId(), stored.getId());
        assertEquals("admin", stored.getUsername());
        assertEquals("pbkdf2-hash", stored.getPasswordHash());
        assertEquals(UserRole.ADMIN, stored.getRole());
    }

    @Test
    void shouldUpdateExistingUserKeepingSameId() {
        User user = user("admin", "old-hash", UserRole.ADMIN);
        userRepository.save(user);

        user.setPasswordHash("new-hash");
        user.setRole(UserRole.GUEST);
        userRepository.save(user);

        User stored = userRepository.findByUsername("admin");

        assertEquals(user.getId(), stored.getId());
        assertEquals("new-hash", stored.getPasswordHash());
        assertEquals(UserRole.GUEST, stored.getRole());
    }

    @Test
    void shouldReturnNullForMissingUsername() {
        assertNull(userRepository.findByUsername("missing"));
    }

    private User user(String username, String passwordHash, UserRole role) {
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordHash);
        user.setRole(role);
        return user;
    }

    private static class TestConnectionProvider extends ConnectionProvider {
        private final String databaseUrl;

        TestConnectionProvider(Path databasePath) {
            this.databaseUrl = "jdbc:sqlite:" + databasePath;
        }

        @Override
        public Connection getConnection() throws SQLException {
            return DriverManager.getConnection(databaseUrl);
        }

        @Override
        public String getDatabaseUrl() {
            return databaseUrl;
        }
    }
}
