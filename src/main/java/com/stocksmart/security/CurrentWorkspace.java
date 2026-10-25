package com.stocksmart.security;

import com.stocksmart.user.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class CurrentWorkspace {
    private final AppUserRepository users;
    public CurrentWorkspace(AppUserRepository users){this.users=users;}
    public AppUser user(Authentication auth){return users.findByEmailIgnoreCase(auth.getName()).orElseThrow();}
    public Long organizationId(Authentication auth){return user(auth).getOrganization().getId();}
}
