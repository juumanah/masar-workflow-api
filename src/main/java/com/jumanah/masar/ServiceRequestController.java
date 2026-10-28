package com.jumanah.masar;
//recieve http reqs and decide wht response to return

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
//now lets add get to retrieve one specific req..

import org.springframework.web.bind.annotation.PathVariable;

import jakarta.validation.Valid;

//Hey u , hope u have a great life..//

@RestController
//here we handle web reqs and returns data
public class ServiceRequestController {

    private final ServiceRequestService service;

    //Spring gives this controller the service through the constructor
    public ServiceRequestController(ServiceRequestService service) {
        this.service = service;
    }

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

    //validate input, then let the service create and store the req
    @PostMapping("/api/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceRequest createRequest(
            @Valid @RequestBody CreateServiceRequest input) {
        return service.createRequest(input);
    }

    //get all reqs from the service, ordered by id
    @GetMapping("/api/requests")
    public List<ServiceRequest> getAllRequests() {
        return service.getAllRequests();
    }

    //retrieve 1 service req using id provided in url
    //ex: GET /api/requests/1 this will seek for the req with id1 =)
    @GetMapping("/api/requests/{id}")
    public ServiceRequest getRequestById(@PathVariable("id") Long id) {
        //the service finds the req or throws a 404 if it doesn't exist
        return service.getRequestById(id);
    }
}