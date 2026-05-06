package com.biblioteca.repository;

import com.biblioteca.domain.Location;

public interface LocationRepository {
    Location findById(Long id);
    Location findByCode(String code);
    Location save(Location location);
}
