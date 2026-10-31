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

import java.time.Instant;
import jakarta.persistence.PrePersist;

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
    
    this.status = "APPROVED";
    this.decidedAt = Instant.now();
}

//reject a submitted req and record the reason
public void reject(String reason) {
    ensureSubmitted();
    this.status = "REJECTED";
    this.rejectionReason = reason.trim();
    this.decidedAt = Instant.now();
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
   //when the req was created and when a decision was made
@Column(updatable = false)
private Instant createdAt;

private Instant decidedAt;

//why the req was rejected??
@Column(columnDefinition = "text")
private String rejectionReason;

//set creation time before saving a new req
@PrePersist
protected void onCreate() {
    this.createdAt = Instant.now();
}

public Instant getCreatedAt() {
    return createdAt;
}

public Instant getDecidedAt() {
    return decidedAt;
    
}
public String getRejectionReason() {
    return rejectionReason;
}
}