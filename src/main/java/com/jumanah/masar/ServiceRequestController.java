package com.jumanah.masar;
//recieve http reqs and decide wht response to return


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.List;
import java.util.Comparator;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
//now lets add get to retrieve one specific req..

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
// 

//Hey u , hope u have a great life..//



@RestController
//here we handle web reqs and returns data
public class ServiceRequestController {
 private final Map<Long, ServiceRequest> requests =
            new ConcurrentHashMap<>();

    private final AtomicLong nextId = new AtomicLong(1);

     //get req, run this method
    @GetMapping("/api/requests/sample")
    public ServiceRequest getSampleRequest() {
        return new ServiceRequest(
                1L,
                "New laptop",
                "A laptop for development work",
                "SUBMITTED"
        );
    }

    @PostMapping("/api/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceRequest createRequest(
            @RequestBody CreateServiceRequest input) {

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
      @GetMapping("/api/requests")
public List<ServiceRequest> getAllRequests() {
    return requests.values()
            .stream()
            .sorted(Comparator.comparing(ServiceRequest::id))
            .toList();
}
//retrieve 1 service req using id provided in url
//ex: GET /api/request/1 this will seek or the req with id1 =)
@GetMapping("/api/requests/{id}")
public ServiceRequest getRequestById(@PathVariable("id") Long id) {
    ServiceRequest request = requests.get(id);

    if (request == null) {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Request not found"
        );
    }

    return request;
}
}