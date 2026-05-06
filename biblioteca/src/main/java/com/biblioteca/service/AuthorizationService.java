package com.biblioteca.service;

import com.biblioteca.security.UserRole;
import com.biblioteca.security.UserSession;

public class AuthorizationService {
    public boolean canManageCatalog(UserSession session) {
        return session != null
                && session.isActive()
                && session.getCurrentUser() != null
                && session.getCurrentUser().getRole() == UserRole.ADMIN;
    }

    public boolean isGuest(UserSession session) {
        return session != null
                && session.getCurrentUser() != null
                && session.getCurrentUser().getRole() == UserRole.GUEST;
    }
}
