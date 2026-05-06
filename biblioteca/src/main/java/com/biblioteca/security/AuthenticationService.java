package com.biblioteca.security;

import com.biblioteca.repository.UserRepository;

public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public AuthenticationService(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    public UserSession login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null || !passwordService.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciales invalidas");
        }

        // Si el usuario aun estaba en formato legado, se migra al hash al iniciar sesion.
        if (passwordService.needsRehash(user.getPasswordHash())) {
            user.setPasswordHash(passwordService.hashPassword(password));
            userRepository.save(user);
        }
        return new UserSession(user, true);
    }

    public UserSession loginAsGuest() {
        User user = new User();
        user.setUsername("guest");
        user.setRole(UserRole.GUEST);
        return new UserSession(user, true);
    }

    public void logout() {
    }
}
