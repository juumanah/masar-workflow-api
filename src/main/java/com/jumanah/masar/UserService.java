package com.jumanah.masar;

import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository repository,
            PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    //register a requester with a hashed password
    public UserResponse register(RegisterUserRequest input) {
        String email = input.email().strip().toLowerCase(Locale.ROOT);

        if (repository.existsByEmail(email)) {
            throw emailAlreadyRegistered();
        }

        String passwordHash = passwordEncoder.encode(input.password());
        UserEntity user = new UserEntity(email, passwordHash);

        UserEntity saved;

        try {
            saved = repository.saveAndFlush(user);
        } catch (DataIntegrityViolationException exception) {
            //handle simultaneous registrations with the same email
            if (repository.existsByEmail(email)) {
                throw emailAlreadyRegistered();
            }
            throw exception;
        }

        return new UserResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getRole()
        );
    }

    private ResponseStatusException emailAlreadyRegistered() {
        return new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Email is already registered"
        );
    }
    //return safe details for the signedin user
public UserResponse getCurrentUser(String email) {
    UserEntity user = repository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User not found"
            ));

    return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getRole()
    );
}
}