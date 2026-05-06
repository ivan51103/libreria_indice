package com.biblioteca.repository.sqlite;

import com.biblioteca.config.ConnectionProvider;
import java.sql.Connection;
import java.sql.SQLException;

abstract class SQLiteSupport {
    private final ConnectionProvider connectionProvider;

    protected SQLiteSupport(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    protected Connection getConnection() throws SQLException {
        return connectionProvider.getConnection();
    }
}
