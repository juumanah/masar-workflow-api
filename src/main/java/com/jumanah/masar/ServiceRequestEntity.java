package com.jumanah.masar;

//here we mapped the db table

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

// Represents one saved req in the database
@Entity
@Table(name = "service_requests")
public class ServiceRequestEntity {

    // Postgresql generates the id when a new req is save
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "text")
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    private String status;

    // JPA needs an empty constructor to load requests from the database
    protected ServiceRequestEntity() {
    }

    //new reqs always start with SUBMITTED status
    public ServiceRequestEntity(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = "SUBMITTED";
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }
    //approve only a submitted req
public void approve() {
    ensureSubmitted();
    this.status = "APPROVED";
}

//reject only a submitted req
public void reject() {
    ensureSubmitted();
    this.status = "REJECTED";
}

//prevent changing a decision already made
private void ensureSubmitted() {
    if (!"SUBMITTED".equals(this.status)) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Only submitted requests can be approved or rejected"
        );
    }
}
}