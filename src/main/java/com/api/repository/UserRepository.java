package com.api.repository;

import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.api.model.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    @Query("UPDATE User u SET u.attempt = ?1 WHERE u.email = ?2")
    @Modifying
    @Transactional
    public void updateFailedAttempts(int failAttempts, String email);
}
