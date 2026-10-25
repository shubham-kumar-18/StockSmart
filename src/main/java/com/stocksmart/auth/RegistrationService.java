package com.stocksmart.auth;

import com.stocksmart.organization.*;
import com.stocksmart.user.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {
    private final OrganizationRepository organizations;
    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;
    public RegistrationService(OrganizationRepository organizations, AppUserRepository users, PasswordEncoder passwordEncoder) {
        this.organizations = organizations; this.users = users; this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(RegistrationForm form) {
        String email = form.getEmail().trim().toLowerCase();
        if (users.existsByEmailIgnoreCase(email)) throw new IllegalArgumentException("An account with that email already exists.");
        if (!form.getPassword().equals(form.getConfirmPassword())) throw new IllegalArgumentException("Passwords do not match.");
        var organization = organizations.save(new Organization(form.getOrganizationName().trim()));
        users.save(new AppUser(organization, form.getFullName().trim(), email, passwordEncoder.encode(form.getPassword())));
    }
}
