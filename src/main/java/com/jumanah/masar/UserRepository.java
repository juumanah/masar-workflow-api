package com.jumanah.masar;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

//save users and find them by email
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}