package com.paulotech.userserver.repository;

import com.paulotech.userserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
    boolean existingEmail(@Param("email") String email);


    boolean existsByKeycloakId(String keycloakId);


    User findByEmail(String email);
}