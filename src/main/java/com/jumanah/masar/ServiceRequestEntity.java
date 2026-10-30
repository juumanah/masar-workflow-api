package com.jumanah.masar;

//here we mapped the db table

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    // New reqs always start with SUBMITTED status
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
}