package com.jumanah.masar;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;

@RestController
public class AuthController {

    private final UserService service;

    public AuthController(UserService service) {
        this.service = service;
    }

    //validate input and register a requester
    @PostMapping("/api/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(
            @Valid @RequestBody RegisterUserRequest input) {
        return service.register(input);
    }
    //provide the csrf token for registration and login
    @GetMapping("/api/auth/csrf")
      public CsrfToken csrf(CsrfToken token) {
        return token;
      }
      //show the signed-in account without exposing its password
@GetMapping("/api/auth/me")
 public UserResponse me(Principal principal) {
    return service.getCurrentUser(principal.getName());

    }
    
}

