package com.jumanah.masar;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class ServiceRequestEntityTest {

    @Test
    void submittedRequestCanBeApproved() {
        ServiceRequestEntity request =
                new ServiceRequestEntity("Laptop", "For development");

        request.approve();

        assertEquals("APPROVED", request.getStatus());
        assertNotNull(request.getDecidedAt());
    }

    @Test
    void rejectedRequestCannotBeApproved() {
        ServiceRequestEntity request =
                new ServiceRequestEntity("Laptop", "For development");

        request.reject("Budget unavailable");
        var decisionTime = request.getDecidedAt();

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                request::approve
        );

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
        assertEquals("REJECTED", request.getStatus());
        assertEquals("Budget unavailable", request.getRejectionReason());
        assertEquals(decisionTime, request.getDecidedAt());
    }
}