package com.biblioteca.security;

public class UserSession {
    private final User currentUser;
    private final boolean active;

    public UserSession(User currentUser, boolean active) {
        this.currentUser = currentUser;
        this.active = active;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isActive() {
        return active;
    }
}
