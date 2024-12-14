package com.example.bookhub.service;

import com.example.bookhub.enums.Genre;
import com.example.bookhub.enums.Language;
import com.example.bookhub.exception.EntityNotFoundException;
import com.example.bookhub.model.dto.BookCreateDTO;
import com.example.bookhub.model.entity.Book;
import com.example.bookhub.model.entity.Review;
import com.example.bookhub.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    public Page<Book> searchBooks(String query, Genre genre, Language language, Pageable pageable) {
        if ((query == null || query.trim().isEmpty()) && genre == null && language == null) {
            return bookRepository.findAll(pageable);
        }
        return bookRepository.searchBooks(query, genre, language, pageable);
    }

    public Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Książka o podanym ID nie została znaleziona."));
    }

    public void addBook(@Validated BookCreateDTO bookCreateDTO) {
        Book book = new Book();

        book.setTitle(bookCreateDTO.getTitle());
        book.setAuthor(bookCreateDTO.getAuthor());
        book.setPublisher(bookCreateDTO.getPublisher());
        book.setIsbn(bookCreateDTO.getIsbn());
        book.setPublicationYear(bookCreateDTO.getPublicationYear());
        book.setGenre(bookCreateDTO.getGenre());
        book.setPageCount(bookCreateDTO.getPageCount());
        book.setLanguage(bookCreateDTO.getLanguage());
        book.setDescription(bookCreateDTO.getDescription());
        book.setImagePath(bookCreateDTO.getImagePath());

        bookRepository.save(book);
    }

    public void updateBookRatings(Book book) {
        List<Review> reviews = book.getReviews();
        int totalRatings = reviews.size();
        double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        book.setNumberOfRatings(totalRatings);
        book.setAverageRating(averageRating);
        bookRepository.save(book);
    }
}
