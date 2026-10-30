package com.jumanah.masar;
// concise data type for holding data;defiining the data structure                 
public record ServiceRequest(
    Long id,
    String title,
    String description,
    String status
) {}