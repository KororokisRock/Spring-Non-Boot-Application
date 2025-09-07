package com.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    void deleteByUsername(String username);

    @Query("SELECT u.id FROM User u WHERE username = :username")
    public Optional<Integer> findUserIdByUsername(@Param("username") String username);
}
