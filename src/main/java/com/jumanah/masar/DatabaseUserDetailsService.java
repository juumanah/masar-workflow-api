package com.jumanah.masar;

import java.util.Locale;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    public DatabaseUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    //use email as the login username
    @Override
    public UserDetails loadUserByUsername(String username) {
        String email = username.strip().toLowerCase(Locale.ROOT);

        UserEntity user = repository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        return User.withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .roles(user.getRole().name())
                .build();
    }
}