package com.biblioteca.repository.memory;

import com.biblioteca.domain.Location;
import com.biblioteca.repository.LocationRepository;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryLocationRepository implements LocationRepository {
    private final Map<Long, Location> storage = new LinkedHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    @Override
    public Location findById(Long id) {
        return storage.get(id);
    }

    @Override
    public Location findByCode(String code) {
        return storage.values().stream()
                .filter(location -> code.equals(location.getCode()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Location save(Location location) {
        if (location.getId() == null) {
            location.setId(sequence.incrementAndGet());
        }
        storage.put(location.getId(), location);
        return location;
    }
}
