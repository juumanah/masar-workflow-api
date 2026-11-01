package com.jumanah.masar;

import jakarta.persistence.*;

@Entity
@Table(name = "app_users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    //store only the encoded password
    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    //required by JPA
    protected UserEntity() {
    }

    //new registrations always receive the requester role
    public UserEntity(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = UserRole.REQUESTER;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserRole getRole() {
        return role;
    }
}