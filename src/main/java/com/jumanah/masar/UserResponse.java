package com.jumanah.masar;

//safe user details returned by the API
public record UserResponse(
        Long id,
        String email,
        UserRole role
) {}
