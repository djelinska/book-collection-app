package com.example.bookhub.repository;

import com.example.bookhub.enums.Genre;
import com.example.bookhub.enums.Language;
import com.example.bookhub.model.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT b FROM Book b WHERE " +
            "(:query IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
            "(:genre IS NULL OR b.genre = :genre) AND " +
            "(:language IS NULL OR b.language = :language)")
    Page<Book> searchBooks(@Param("query") String query,
                           @Param("genre") Genre genre,
                           @Param("language") Language language,
                           Pageable pageable);
}
