package com.jumanah.masar;

public record ServiceRequest(
    Long id,
    String title,
    String description,
    String status
) {}