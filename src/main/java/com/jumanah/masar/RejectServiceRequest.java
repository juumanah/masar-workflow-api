package com.jumanah.masar;

import jakarta.validation.constraints.NotBlank;

//require a reason when rejecting a req<3
public record RejectServiceRequest(
        @NotBlank(message = "Rejection reason is required")
        String reason
) {}