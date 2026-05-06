package com.biblioteca.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.biblioteca.repository.memory.InMemoryUserRepository;
import org.junit.jupiter.api.Test;

class AuthenticationServiceTest {
    @Test
    void shouldLoginWithHashedPassword() {
        InMemoryUserRepository userRepository = new InMemoryUserRepository();
        PasswordService passwordService = new PasswordService();
        User admin = userRepository.findByUsername("admin");
        admin.setPasswordHash(passwordService.hashPassword("admin123"));
        userRepository.save(admin);

        AuthenticationService authenticationService = new AuthenticationService(userRepository, passwordService);
        UserSession session = authenticationService.login("admin", "admin123");

        assertNotNull(session);
        assertTrue(session.isActive());
        assertEquals(UserRole.ADMIN, session.getCurrentUser().getRole());
    }

    @Test
    void shouldMigrateLegacyPasswordOnSuccessfulLogin() {
        InMemoryUserRepository userRepository = new InMemoryUserRepository();
        PasswordService passwordService = new PasswordService();
        AuthenticationService authenticationService = new AuthenticationService(userRepository, passwordService);

        UserSession session = authenticationService.login("admin", "admin123");
        User storedUser = userRepository.findByUsername("admin");

        assertNotNull(session);
        assertTrue(passwordService.isHashed(storedUser.getPasswordHash()));
        assertTrue(passwordService.matches("admin123", storedUser.getPasswordHash()));
    }

    @Test
    void shouldRejectInvalidCredentials() {
        InMemoryUserRepository userRepository = new InMemoryUserRepository();
        AuthenticationService authenticationService = new AuthenticationService(userRepository, new PasswordService());

        assertThrows(IllegalArgumentException.class, () -> authenticationService.login("admin", "incorrecta"));
    }
}
