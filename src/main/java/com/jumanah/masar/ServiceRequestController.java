package com.jumanah.masar;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceRequestController {

    @GetMapping("/api/requests/sample")
    public ServiceRequest getSampleRequest() {
        return new ServiceRequest(
            1L,
            "New laptop",
            "A laptop for development work",
            "SUBMITTED"
        );
    }
}