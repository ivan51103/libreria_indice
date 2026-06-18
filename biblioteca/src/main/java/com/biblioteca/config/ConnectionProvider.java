package com.biblioteca.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Function;

public class ConnectionProvider {
    private final String databaseUrl;

    public ConnectionProvider() {
        Path databasePath = Path.of(System.getProperty("user.home"), ".biblioteca", "biblioteca.db");
        try {
            Files.createDirectories(databasePath.getParent());
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo crear el directorio para la base de datos en: " + databasePath.getParent(), exception);
        }
        this.databaseUrl = "jdbc:sqlite:" + databasePath;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databaseUrl);
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public <T> T withTransaction(Function<Connection, T> work) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try {
                T result = work.apply(connection);
                try {
                    connection.commit();
                } catch (SQLException exception) {
                    rollbackQuietly(connection);
                    throw exception;
                }
                return result;
            } catch (RuntimeException exception) {
                rollbackQuietly(connection);
                throw exception;
            } catch (Error error) {
                rollbackQuietly(connection);
                throw error;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo abrir una transaccion SQLite", exception);
        }
    }

    private void rollbackQuietly(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException ignored) {
            // Se prioriza la excepcion original de negocio o persistencia.
        }
    }
}
