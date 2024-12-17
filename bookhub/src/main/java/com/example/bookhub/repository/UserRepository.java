package com.example.bookhub.repository;

import com.example.bookhub.enums.Genre;
import com.example.bookhub.enums.Language;
import com.example.bookhub.enums.Role;
import com.example.bookhub.model.entity.Book;
import com.example.bookhub.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    Page<User> findByUsernameContainingIgnoreCase(String query, Pageable pageable);

    long countByRole(Role role);
}
