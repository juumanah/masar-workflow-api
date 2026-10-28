package com.jumanah.masar;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

//manage service reqs and keep them in temporary memory
@Service
public class ServiceRequestService {

    private final Map<Long, ServiceRequest> requests =
            new ConcurrentHashMap<>();

    private final AtomicLong nextId = new AtomicLong(1);

    //give each req a unique id, store it, then return it
    public ServiceRequest createRequest(CreateServiceRequest input) {
        long id = nextId.getAndIncrement();

        ServiceRequest request = new ServiceRequest(
                id,
                input.title(),
                input.description(),
                "SUBMITTED"
        );

        requests.put(id, request);
        return request;
    }

    //get all stored reqs ordered by id
    public List<ServiceRequest> getAllRequests() {
        return requests.values()
                .stream()
                .sorted(Comparator.comparing(ServiceRequest::id))
                .toList();
    }

    //find one req using its id
    public ServiceRequest getRequestById(Long id) {
        ServiceRequest request = requests.get(id);

        //if the req doesn't exist, return 404
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Request not found"
            );
        }

        return request;
    }
}