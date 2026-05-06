package com.biblioteca.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.biblioteca.security.User;
import com.biblioteca.security.UserRole;
import com.biblioteca.security.UserSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuthorizationServiceTest {
    private AuthorizationService authorizationService;

    @BeforeEach
    void setUp() {
        authorizationService = new AuthorizationService();
    }

    @Test
    void shouldAllowOnlyActiveAdminToManageCatalog() {
        assertTrue(authorizationService.canManageCatalog(session(UserRole.ADMIN, true)));
        assertFalse(authorizationService.canManageCatalog(session(UserRole.ADMIN, false)));
        assertFalse(authorizationService.canManageCatalog(session(UserRole.GUEST, true)));
        assertFalse(authorizationService.canManageCatalog(null));
    }

    @Test
    void shouldIdentifyGuestSession() {
        assertTrue(authorizationService.isGuest(session(UserRole.GUEST, true)));
        assertFalse(authorizationService.isGuest(session(UserRole.ADMIN, true)));
        assertFalse(authorizationService.isGuest(null));
    }

    private UserSession session(UserRole role, boolean active) {
        User user = new User();
        user.setUsername(role.name().toLowerCase());
        user.setRole(role);
        return new UserSession(user, active);
    }
}
