package com.biblioteca.repository.sqlite;

import com.biblioteca.config.ConnectionProvider;
import com.biblioteca.repository.UserRepository;
import com.biblioteca.security.User;
import com.biblioteca.security.UserRole;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLiteUserRepository extends SQLiteSupport implements UserRepository {
    public SQLiteUserRepository(ConnectionProvider connectionProvider) {
        super(connectionProvider);
    }

    @Override
    public User findByUsername(String username) {
        String sql = """
            SELECT id, username, password_hash, role
            FROM users
            WHERE username = ?
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return mapUser(resultSet);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo consultar el usuario", exception);
        }
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            return insert(user);
        }
        return update(user);
    }

    private User insert(User user) {
        String sql = """
            INSERT INTO users (username, password_hash, role)
            VALUES (?, ?, ?)
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPasswordHash());
            statement.setString(3, user.getRole().name());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getLong(1));
                }
            }
            return user;
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo registrar el usuario", exception);
        }
    }

    private User update(User user) {
        String sql = """
            UPDATE users
            SET username = ?, password_hash = ?, role = ?
            WHERE id = ?
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPasswordHash());
            statement.setString(3, user.getRole().name());
            statement.setLong(4, user.getId());
            statement.executeUpdate();
            return user;
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo actualizar el usuario", exception);
        }
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setUsername(resultSet.getString("username"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setRole(UserRole.valueOf(resultSet.getString("role")));
        return user;
    }
}
