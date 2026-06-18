package com.biblioteca.repository.sqlite;

import com.biblioteca.config.ConnectionProvider;
import com.biblioteca.domain.BookCopy;
import com.biblioteca.domain.CopyStatus;
import com.biblioteca.repository.BookCopyRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLiteBookCopyRepository extends SQLiteSupport implements BookCopyRepository {
    public SQLiteBookCopyRepository(ConnectionProvider connectionProvider) {
        super(connectionProvider);
    }

    @Override
    public BookCopy findById(Long id) {
        String sql = """
            SELECT id, book_title_id, inventory_code, location_id, status, notes
            FROM book_copies
            WHERE id = ?
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return mapCopy(resultSet);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo consultar el ejemplar", exception);
        }
    }

    @Override
    public List<BookCopy> findByBookTitleId(Long bookTitleId) {
        String sql = """
            SELECT id, book_title_id, inventory_code, location_id, status, notes
            FROM book_copies
            WHERE book_title_id = ?
            ORDER BY inventory_code
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, bookTitleId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<BookCopy> copies = new ArrayList<>();
                while (resultSet.next()) {
                    copies.add(mapCopy(resultSet));
                }
                return copies;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudieron consultar los ejemplares", exception);
        }
    }

    @Override
    public BookCopy findByInventoryCode(String inventoryCode) {
        String sql = """
            SELECT id, book_title_id, inventory_code, location_id, status, notes
            FROM book_copies
            WHERE inventory_code = ?
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, inventoryCode);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return mapCopy(resultSet);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo consultar el ejemplar", exception);
        }
    }

    @Override
    public BookCopy save(BookCopy bookCopy) {
        if (bookCopy.getId() == null) {
            return insert(bookCopy);
        }
        return update(bookCopy);
    }

    @Override
    public BookCopy save(BookCopy bookCopy, Connection connection) {
        if (bookCopy.getId() == null) {
            return insert(bookCopy, connection);
        }
        return update(bookCopy, connection);
    }

    @Override
    public void updateStatus(Long copyId, CopyStatus status) {
        try (Connection connection = getConnection()) {
            updateStatus(copyId, status, connection);
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo actualizar el estado del ejemplar", exception);
        }
    }

    @Override
    public void updateStatus(Long copyId, CopyStatus status, Connection connection) {
        String sql = """
            UPDATE book_copies
            SET status = ?
            WHERE id = ?
            """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status.name());
            statement.setLong(2, copyId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo actualizar el estado del ejemplar", exception);
        }
    }

    @Override
    public void delete(Long copyId) {
        String sql = "DELETE FROM book_copies WHERE id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, copyId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo eliminar el ejemplar", exception);
        }
    }

    private BookCopy insert(BookCopy bookCopy) {
        try (Connection connection = getConnection()) {
            return insert(bookCopy, connection);
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo registrar el ejemplar", exception);
        }
    }

    private BookCopy insert(BookCopy bookCopy, Connection connection) {
        String sql = """
            INSERT INTO book_copies (book_title_id, inventory_code, location_id, status, notes)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, bookCopy.getBookTitleId());
            statement.setString(2, bookCopy.getInventoryCode());
            statement.setLong(3, bookCopy.getLocationId());
            statement.setString(4, bookCopy.getStatus().name());
            statement.setString(5, bookCopy.getNotes());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    bookCopy.setId(keys.getLong(1));
                }
            }
            return bookCopy;
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo registrar el ejemplar", exception);
        }
    }

    private BookCopy update(BookCopy bookCopy) {
        try (Connection connection = getConnection()) {
            return update(bookCopy, connection);
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo actualizar el ejemplar", exception);
        }
    }

    private BookCopy update(BookCopy bookCopy, Connection connection) {
        String sql = """
            UPDATE book_copies
            SET book_title_id = ?, inventory_code = ?, location_id = ?, status = ?, notes = ?
            WHERE id = ?
            """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, bookCopy.getBookTitleId());
            statement.setString(2, bookCopy.getInventoryCode());
            statement.setLong(3, bookCopy.getLocationId());
            statement.setString(4, bookCopy.getStatus().name());
            statement.setString(5, bookCopy.getNotes());
            statement.setLong(6, bookCopy.getId());
            statement.executeUpdate();
            return bookCopy;
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo actualizar el ejemplar", exception);
        }
    }

    private BookCopy mapCopy(ResultSet resultSet) throws SQLException {
        BookCopy bookCopy = new BookCopy();
        bookCopy.setId(resultSet.getLong("id"));
        bookCopy.setBookTitleId(resultSet.getLong("book_title_id"));
        bookCopy.setInventoryCode(resultSet.getString("inventory_code"));
        bookCopy.setLocationId(resultSet.getLong("location_id"));
        bookCopy.setStatus(CopyStatus.valueOf(resultSet.getString("status")));
        bookCopy.setNotes(resultSet.getString("notes"));
        return bookCopy;
    }
}
