package com.jumanah.masar;

import org.springframework.data.jpa.repository.JpaRepository;

// Provides db operations for service requests
// ServiceRequestEntity is the stored entity; Long is its id typee
public interface ServiceRequestRepository
        extends JpaRepository<ServiceRequestEntity, Long> {
}