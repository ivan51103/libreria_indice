package com.biblioteca.config;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionProvider {
    private final String databaseUrl;

    public ConnectionProvider() {
        Path databasePath = Path.of("src", "main", "resources", "biblioteca.db");
        this.databaseUrl = "jdbc:sqlite:" + databasePath;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databaseUrl);
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }
}
