package com.biblioteca.repository;

import com.biblioteca.domain.Location;
import java.sql.Connection;

public interface LocationRepository {
    Location findById(Long id);
    Location findByCode(String code);
    Location save(Location location);
    default Location save(Location location, Connection connection) {
        return save(location);
    }
}
