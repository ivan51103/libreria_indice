package com.biblioteca.repository.sqlite;

import com.biblioteca.config.ConnectionProvider;
import com.biblioteca.domain.BookTitle;
import com.biblioteca.repository.BookTitleRepository;
import com.biblioteca.search.query.BookSearchCriteria;
import com.biblioteca.search.query.BookSortField;
import com.biblioteca.search.query.PageRequest;
import com.biblioteca.search.query.PageResult;
import com.biblioteca.search.query.SortDirection;
import java.text.Normalizer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SQLiteBookTitleRepository extends SQLiteSupport implements BookTitleRepository {
    public SQLiteBookTitleRepository(ConnectionProvider connectionProvider) {
        super(connectionProvider);
    }

    @Override
    public PageResult<BookTitle> findAll(PageRequest pageRequest) {
        String sql = """
            SELECT id, title, author, isbn, publisher, year, category, career, description, cover_path
            FROM book_titles
            ORDER BY %s %s
            LIMIT ? OFFSET ?
            """.formatted(resolveSortColumn(pageRequest), resolveSortDirection(pageRequest));

        String countSql = "SELECT COUNT(*) FROM book_titles";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             PreparedStatement countStatement = connection.prepareStatement(countSql)) {
            int size = resolvePageSize(pageRequest);
            int page = resolvePage(pageRequest);

            statement.setInt(1, size);
            statement.setInt(2, page * size);

            List<BookTitle> items = mapBookTitles(statement.executeQuery());
            long totalItems = readCount(countStatement.executeQuery());
            int totalPages = calculateTotalPages(totalItems, size);
            return new PageResult<>(items, totalItems, totalPages, page);
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo consultar el catalogo", exception);
        }
    }

    @Override
    public BookTitle findById(Long id) {
        String sql = """
            SELECT id, title, author, isbn, publisher, year, category, career, description, cover_path
            FROM book_titles
            WHERE id = ?
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return mapBookTitle(resultSet);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo consultar el libro", exception);
        }
    }

    @Override
    public BookTitle findByIsbn(String isbn) {
        String sql = """
            SELECT id, title, author, isbn, publisher, year, category, career, description, cover_path
            FROM book_titles
            WHERE isbn = ?
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, isbn);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return mapBookTitle(resultSet);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo consultar el libro por ISBN", exception);
        }
    }

    @Override
    public PageResult<BookTitle> search(BookSearchCriteria criteria, PageRequest pageRequest) {
        List<BookTitle> filtered = findAllTitles().stream()
                .filter(bookTitle -> matches(bookTitle, criteria))
                .sorted(buildComparator(pageRequest))
                .toList();

        return toPage(filtered, pageRequest);
    }

    @Override
    public BookTitle save(BookTitle bookTitle) {
        if (bookTitle.getId() == null) {
            return insert(bookTitle);
        }
        return update(bookTitle);
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM book_titles WHERE id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo eliminar el libro", exception);
        }
    }

    private BookTitle insert(BookTitle bookTitle) {
        String sql = """
            INSERT INTO book_titles (title, author, isbn, publisher, year, category, career, description, cover_path)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            fillStatement(statement, bookTitle);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    bookTitle.setId(keys.getLong(1));
                }
            }
            return bookTitle;
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo registrar el libro", exception);
        }
    }

    private BookTitle update(BookTitle bookTitle) {
        String sql = """
            UPDATE book_titles
            SET title = ?, author = ?, isbn = ?, publisher = ?, year = ?, category = ?, career = ?, description = ?, cover_path = ?
            WHERE id = ?
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            fillStatement(statement, bookTitle);
            statement.setLong(10, bookTitle.getId());
            statement.executeUpdate();
            return bookTitle;
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo actualizar el libro", exception);
        }
    }

    private void fillStatement(PreparedStatement statement, BookTitle bookTitle) throws SQLException {
        statement.setString(1, bookTitle.getTitle());
        statement.setString(2, bookTitle.getAuthor());
        statement.setString(3, bookTitle.getIsbn());
        statement.setString(4, bookTitle.getPublisher());
        statement.setInt(5, bookTitle.getYear());
        statement.setString(6, bookTitle.getCategory());
        statement.setString(7, bookTitle.getCareer());
        statement.setString(8, bookTitle.getDescription());
        statement.setString(9, bookTitle.getCoverPath());
    }

    private List<BookTitle> findAllTitles() {
        String sql = """
            SELECT id, title, author, isbn, publisher, year, category, career, description, cover_path
            FROM book_titles
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return mapBookTitles(resultSet);
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo consultar la lista completa de libros", exception);
        }
    }

    private List<BookTitle> mapBookTitles(ResultSet resultSet) throws SQLException {
        List<BookTitle> items = new ArrayList<>();
        while (resultSet.next()) {
            items.add(mapBookTitle(resultSet));
        }
        return items;
    }

    private BookTitle mapBookTitle(ResultSet resultSet) throws SQLException {
        BookTitle bookTitle = new BookTitle();
        bookTitle.setId(resultSet.getLong("id"));
        bookTitle.setTitle(resultSet.getString("title"));
        bookTitle.setAuthor(resultSet.getString("author"));
        bookTitle.setIsbn(resultSet.getString("isbn"));
        bookTitle.setPublisher(resultSet.getString("publisher"));
        bookTitle.setYear(resultSet.getInt("year"));
        bookTitle.setCategory(resultSet.getString("category"));
        bookTitle.setCareer(resultSet.getString("career"));
        bookTitle.setDescription(resultSet.getString("description"));
        bookTitle.setCoverPath(resultSet.getString("cover_path"));
        return bookTitle;
    }

    private boolean matches(BookTitle bookTitle, BookSearchCriteria criteria) {
        if (criteria == null) {
            return true;
        }
        return contains(bookTitle.getTitle(), criteria.getText())
                && contains(bookTitle.getAuthor(), criteria.getAuthor())
                && contains(bookTitle.getCategory(), criteria.getCategory())
                && contains(bookTitle.getCareer(), criteria.getCareer());
    }

    private boolean contains(String fieldValue, String criteriaValue) {
        String normalizedCriteria = normalize(criteriaValue);
        if (normalizedCriteria.isBlank()) {
            return true;
        }
        return normalize(fieldValue).contains(normalizedCriteria);
    }

    private String normalize(String text) {
        if (text == null) {
            return "";
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase().trim();
    }

    private PageResult<BookTitle> toPage(List<BookTitle> items, PageRequest pageRequest) {
        int size = resolvePageSize(pageRequest);
        int page = resolvePage(pageRequest);
        int fromIndex = Math.min(page * size, items.size());
        int toIndex = Math.min(fromIndex + size, items.size());
        int totalPages = calculateTotalPages(items.size(), size);
        return new PageResult<>(items.subList(fromIndex, toIndex), items.size(), totalPages, page);
    }

    private int resolvePageSize(PageRequest pageRequest) {
        return pageRequest != null && pageRequest.getSize() > 0 ? pageRequest.getSize() : 10;
    }

    private int resolvePage(PageRequest pageRequest) {
        return pageRequest != null ? Math.max(pageRequest.getPage(), 0) : 0;
    }

    private int calculateTotalPages(long totalItems, int size) {
        if (size <= 0) {
            return 1;
        }
        return (int) Math.ceil((double) totalItems / size);
    }

    private long readCount(ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return 0;
        }
        return resultSet.getLong(1);
    }

    private String resolveSortColumn(PageRequest pageRequest) {
        BookSortField sortField = pageRequest != null && pageRequest.getSortField() != null
                ? pageRequest.getSortField()
                : BookSortField.TITLE;

        return switch (sortField) {
            case AUTHOR -> "author";
            case YEAR -> "year";
            case CATEGORY -> "category";
            case TITLE -> "title";
        };
    }

    private String resolveSortDirection(PageRequest pageRequest) {
        SortDirection direction = pageRequest != null && pageRequest.getDirection() != null
                ? pageRequest.getDirection()
                : SortDirection.ASC;
        return direction == SortDirection.DESC ? "DESC" : "ASC";
    }

    private Comparator<BookTitle> buildComparator(PageRequest pageRequest) {
        BookSortField sortField = pageRequest != null && pageRequest.getSortField() != null
                ? pageRequest.getSortField()
                : BookSortField.TITLE;

        Comparator<BookTitle> comparator = switch (sortField) {
            case AUTHOR -> Comparator.comparing(BookTitle::getAuthor, Comparator.nullsLast(String::compareToIgnoreCase));
            case YEAR -> Comparator.comparingInt(BookTitle::getYear);
            case CATEGORY -> Comparator.comparing(BookTitle::getCategory, Comparator.nullsLast(String::compareToIgnoreCase));
            case TITLE -> Comparator.comparing(BookTitle::getTitle, Comparator.nullsLast(String::compareToIgnoreCase));
        };

        return resolveSortDirection(pageRequest).equals("DESC") ? comparator.reversed() : comparator;
    }
}
