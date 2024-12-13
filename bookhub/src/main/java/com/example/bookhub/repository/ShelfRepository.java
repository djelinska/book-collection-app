package com.example.bookhub.repository;

import com.example.bookhub.enums.ShelfType;
import com.example.bookhub.model.entity.Book;
import com.example.bookhub.model.entity.Shelf;
import com.example.bookhub.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Long> {
    List<Shelf> findByUser(User user);

    Shelf findByNameAndUser(String name, User user);

    int countByBooksContainingAndType(Book book, ShelfType type);

    void deleteAllByUser(User user);
}