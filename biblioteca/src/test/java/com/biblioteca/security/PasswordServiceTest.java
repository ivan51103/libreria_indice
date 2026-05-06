package com.biblioteca.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PasswordServiceTest {
    private final PasswordService passwordService = new PasswordService();

    @Test
    void shouldHashAndValidatePassword() {
        String hash = passwordService.hashPassword("admin123");

        assertTrue(passwordService.isHashed(hash));
        assertTrue(passwordService.matches("admin123", hash));
        assertFalse(passwordService.matches("otra-clave", hash));
    }

    @Test
    void shouldGenerateDifferentHashesForSamePassword() {
        String firstHash = passwordService.hashPassword("admin123");
        String secondHash = passwordService.hashPassword("admin123");

        assertNotEquals(firstHash, secondHash);
        assertTrue(passwordService.matches("admin123", firstHash));
        assertTrue(passwordService.matches("admin123", secondHash));
    }

    @Test
    void shouldMarkLegacyPasswordForRehash() {
        assertTrue(passwordService.needsRehash("admin123"));
        assertFalse(passwordService.needsRehash(passwordService.hashPassword("admin123")));
    }
}
