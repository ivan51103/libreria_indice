package com.biblioteca.ui.controller;

import com.biblioteca.security.AuthenticationService;
import com.biblioteca.security.UserSession;

public class LoginController {
    private final AuthenticationService authenticationService;

    public LoginController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public UserSession loginAsGuest() {
        return authenticationService.loginAsGuest();
    }

    public UserSession login(String username, String password) {
        return authenticationService.login(username, password);
    }
}
