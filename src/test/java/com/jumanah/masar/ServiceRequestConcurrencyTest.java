package com.jumanah.masar;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ServiceRequestConcurrencyTest {

    @Autowired
    private ServiceRequestRepository repository;

    @Test
    void staleDecisionCannotOverwriteSavedDecision() {
        ServiceRequestEntity saved = repository.saveAndFlush(
                new ServiceRequestEntity(
                        "Concurrency test",
                        "Prevent conflicting decisions"
                )
        );

        Long id = saved.getId();

        try {
            //load separate copies with the same version
            ServiceRequestEntity first =
                    repository.findById(id).orElseThrow();
            ServiceRequestEntity second =
                    repository.findById(id).orElseThrow();

            //save the first decision
            first.approve();
            repository.saveAndFlush(first);

            //the second copy still has the old version
            second.reject("Conflicting decision");

            assertThrows(
                    OptimisticLockingFailureException.class,
                    () -> repository.saveAndFlush(second)
            );

            //confirm the first decision remains saved
            ServiceRequestEntity actual =
                    repository.findById(id).orElseThrow();

            assertEquals("APPROVED", actual.getStatus());
            assertNull(actual.getRejectionReason());
            assertNotNull(actual.getDecidedAt());
        } finally {
            //remove only the request created by this test
            repository.deleteById(id);
        }
    }
}