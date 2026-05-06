package com.biblioteca.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private final ConnectionProvider connectionProvider;

    public DatabaseInitializer(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public void initialize() {
        try (Connection connection = connectionProvider.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password_hash TEXT NOT NULL,
                    role TEXT NOT NULL
                )
                """);

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS book_titles (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    author TEXT NOT NULL,
                    isbn TEXT UNIQUE,
                    publisher TEXT,
                    year INTEGER,
                    category TEXT,
                    career TEXT,
                    description TEXT,
                    cover_path TEXT
                )
                """);

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS locations (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    room TEXT NOT NULL,
                    section TEXT NOT NULL,
                    shelf TEXT NOT NULL,
                    level TEXT,
                    position TEXT,
                    code TEXT NOT NULL UNIQUE
                )
                """);

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS book_copies (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    book_title_id INTEGER NOT NULL,
                    inventory_code TEXT NOT NULL UNIQUE,
                    location_id INTEGER NOT NULL,
                    status TEXT NOT NULL,
                    notes TEXT,
                    FOREIGN KEY (book_title_id) REFERENCES book_titles(id),
                    FOREIGN KEY (location_id) REFERENCES locations(id)
                )
                """);
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo inicializar la base SQLite", exception);
        }
    }
}
