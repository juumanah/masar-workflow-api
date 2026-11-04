package com.jumanah.masar;

import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRequestRepository
        extends JpaRepository<ServiceRequestEntity, Long> {

    List<ServiceRequestEntity> findAllByOwner_IdOrderByIdAsc(Long ownerId);

    Optional<ServiceRequestEntity> findByIdAndOwner_Id(Long id, Long ownerId);
}