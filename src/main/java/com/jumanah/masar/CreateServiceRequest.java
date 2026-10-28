package com.jumanah.masar;

import jakarta.validation.constraints.NotBlank;

public record CreateServiceRequest(
        // Reject null,empty,or whitespace only titles
        @NotBlank(message = "Title is required")
        String title,

        // A desc must contain at least one nonwhitespace character
        @NotBlank(message = "Description is required")
        String description
) {}