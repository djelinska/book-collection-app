package com.example.bookhub.repository;

import com.example.bookhub.model.entity.Review;
import com.example.bookhub.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByContentContainingIgnoreCase(String query, Pageable pageable);
}
