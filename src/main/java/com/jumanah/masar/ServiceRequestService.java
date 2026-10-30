package com.jumanah.masar;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.transaction.annotation.Transactional;
//manage service reqs and store them in PostgreSQL
@Service
public class ServiceRequestService {

    private final ServiceRequestRepository repository;

    //spring provides the repository through this constructor
    public ServiceRequestService(ServiceRequestRepository repository) {
        this.repository = repository;
    }

    //save the req.. PostgreSQL generates its unique id
    public ServiceRequest createRequest(CreateServiceRequest input) {
        ServiceRequestEntity entity = new ServiceRequestEntity(
                input.title(),
                input.description()
        );

        ServiceRequestEntity saved = repository.save(entity);
        return toResponse(saved);
    }

    //get all stored reqs ordered by id
    public List<ServiceRequest> getAllRequests() {
        return repository.findAll(Sort.by("id").ascending())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    //find one req using its id
    public ServiceRequest getRequestById(Long id) {
        //if the req doesn't exist, return 404
        ServiceRequestEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Request not found"
                ));

        return toResponse(entity);
    }

    //convert the database entity into the API response
    private ServiceRequest toResponse(ServiceRequestEntity entity) {
        return new ServiceRequest(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus()
        );
    }
    //approve the req and save the change
@Transactional
public ServiceRequest approveRequest(Long id) {
    ServiceRequestEntity entity = findRequest(id);
    entity.approve();
    return toResponse(entity);
}

//reject the req and save the change
@Transactional
public ServiceRequest rejectRequest(Long id) {
    ServiceRequestEntity entity = findRequest(id);
    entity.reject();
    return toResponse(entity);
}

//find the saved req or return 404
private ServiceRequestEntity findRequest(Long id) {
    return repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Request not found"
            ));
}
}