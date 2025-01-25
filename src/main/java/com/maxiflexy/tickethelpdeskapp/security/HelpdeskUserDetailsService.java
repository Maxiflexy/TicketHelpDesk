package com.maxiflexy.tickethelpdeskapp.security;

import com.infometics.helpdesk.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
public class HelpdeskUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public HelpdeskUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return userService.fetchUserByEmail(username);
        } catch (EntityNotFoundException e) {
            throw new UsernameNotFoundException("Invalid username or password");
        }
    }
}
