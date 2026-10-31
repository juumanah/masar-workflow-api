package com.jumanah.masar;
// concise data type for holding data;defiining the data structure                 
import java.time.Instant;
//data returned by the API
public record ServiceRequest(
        Long id,
        String title,
        String description,
        String status,
        Instant createdAt,
        Instant decidedAt,
        String rejectionReason
) {}