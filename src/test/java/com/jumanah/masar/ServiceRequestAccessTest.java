package com.jumanah.masar;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ServiceRequestAccessTest {

    @Autowired
    private ServiceRequestService service;

    @Autowired
    private UserRepository users;

    //test data is rolled back after each test
    private UserEntity createUser() {
        return users.save(new UserEntity(
                UUID.randomUUID() + "@example.com",
                "unused-test-hash"
        ));
    }

    //simulate an authenticated user for service-level tests
    private void signIn(UserEntity user) {
        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(), null, List.of()
                )
        );
        SecurityContextHolder.setContext(context);
    }

    @AfterEach
    void clearSession() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void requesterCanReadOwnRequest() {
        UserEntity owner = createUser();
        signIn(owner);

        ServiceRequest created = service.createRequest(
                new CreateServiceRequest("Laptop", "For development")
        );

        assertEquals(created.id(),
                service.getRequestById(created.id()).id());

        assertEquals(
                List.of(created.id()),
                service.getAllRequests().stream()
                        .map(ServiceRequest::id).toList()
        );
    }

    @Test
    void requesterCannotReadAnotherUsersRequest() {
        UserEntity owner = createUser();
        UserEntity other = createUser();

        signIn(owner);
        ServiceRequest created = service.createRequest(
                new CreateServiceRequest("Private request", "Owner only")
        );

        signIn(other);

        assertTrue(service.getAllRequests().isEmpty());

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.getRequestById(created.id())
        );

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
    }

    @Test
    void requesterCannotApproveOrRejectEvenOwnRequest() {
        signIn(createUser());

        ServiceRequest created = service.createRequest(
                new CreateServiceRequest("Laptop", "Needs a decision")
        );

        ResponseStatusException approvalError = assertThrows(
                ResponseStatusException.class,
                () -> service.approveRequest(created.id())
        );

        ResponseStatusException rejectionError = assertThrows(
                ResponseStatusException.class,
                () -> service.rejectRequest(
                        created.id(),
                        new RejectServiceRequest("Not allowed")
                )
        );

        assertEquals(HttpStatus.FORBIDDEN, approvalError.getStatusCode());
        assertEquals(HttpStatus.FORBIDDEN, rejectionError.getStatusCode());
        assertEquals("SUBMITTED",
                service.getRequestById(created.id()).status());
    }
}