package com.biblioteca.repository.sqlite;

import com.biblioteca.config.ConnectionProvider;
import com.biblioteca.domain.Location;
import com.biblioteca.repository.LocationRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLiteLocationRepository extends SQLiteSupport implements LocationRepository {
    public SQLiteLocationRepository(ConnectionProvider connectionProvider) {
        super(connectionProvider);
    }

    @Override
    public Location findById(Long id) {
        String sql = """
            SELECT id, room, section, shelf, level, position, code
            FROM locations
            WHERE id = ?
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return mapLocation(resultSet);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo consultar la ubicacion", exception);
        }
    }

    @Override
    public Location findByCode(String code) {
        String sql = """
            SELECT id, room, section, shelf, level, position, code
            FROM locations
            WHERE code = ?
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, code);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return mapLocation(resultSet);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo consultar la ubicacion por codigo", exception);
        }
    }

    @Override
    public Location save(Location location) {
        if (location.getId() == null) {
            return insert(location);
        }
        return update(location);
    }

    @Override
    public Location save(Location location, Connection connection) {
        if (location.getId() == null) {
            return insert(location, connection);
        }
        return update(location, connection);
    }

    private Location insert(Location location) {
        try (Connection connection = getConnection()) {
            return insert(location, connection);
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo registrar la ubicacion", exception);
        }
    }

    private Location insert(Location location, Connection connection) {
        String sql = """
            INSERT INTO locations (room, section, shelf, level, position, code)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, location.getRoom());
            statement.setString(2, location.getSection());
            statement.setString(3, location.getShelf());
            statement.setString(4, location.getLevel());
            statement.setString(5, location.getPosition());
            statement.setString(6, location.getCode());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    location.setId(keys.getLong(1));
                }
            }
            return location;
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo registrar la ubicacion", exception);
        }
    }

    private Location update(Location location) {
        try (Connection connection = getConnection()) {
            return update(location, connection);
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo actualizar la ubicacion", exception);
        }
    }

    private Location update(Location location, Connection connection) {
        String sql = """
            UPDATE locations
            SET room = ?, section = ?, shelf = ?, level = ?, position = ?, code = ?
            WHERE id = ?
            """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, location.getRoom());
            statement.setString(2, location.getSection());
            statement.setString(3, location.getShelf());
            statement.setString(4, location.getLevel());
            statement.setString(5, location.getPosition());
            statement.setString(6, location.getCode());
            statement.setLong(7, location.getId());
            statement.executeUpdate();
            return location;
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo actualizar la ubicacion", exception);
        }
    }

    private Location mapLocation(ResultSet resultSet) throws SQLException {
        Location location = new Location();
        location.setId(resultSet.getLong("id"));
        location.setRoom(resultSet.getString("room"));
        location.setSection(resultSet.getString("section"));
        location.setShelf(resultSet.getString("shelf"));
        location.setLevel(resultSet.getString("level"));
        location.setPosition(resultSet.getString("position"));
        location.setCode(resultSet.getString("code"));
        return location;
    }
}
