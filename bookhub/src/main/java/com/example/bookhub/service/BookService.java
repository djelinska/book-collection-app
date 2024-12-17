package com.example.bookhub.service;

import com.example.bookhub.BookSpecifications;
import com.example.bookhub.enums.Genre;
import com.example.bookhub.enums.Language;
import com.example.bookhub.exception.EntityNotFoundException;
import com.example.bookhub.model.dto.AdminBookCreateDTO;
import com.example.bookhub.model.dto.AdminBookUpdateDTO;
import com.example.bookhub.model.entity.Book;
import com.example.bookhub.model.entity.Review;
import com.example.bookhub.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Page<Book> searchBooks(String query, Genre genre, Language language, Pageable pageable) {
        Specification<Book> spec = Specification
                .where(BookSpecifications.hasQuery(query))
                .and(BookSpecifications.hasGenre(genre))
                .and(BookSpecifications.hasLanguage(language));

        return bookRepository.findAll(spec, pageable);
    }

    public Page<Book> searchBooks(String query, Pageable pageable) {
        Specification<Book> spec = BookSpecifications.hasQuery(query);
        return bookRepository.findAll(spec, pageable);
    }

    public Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Książka o podanym ID nie została znaleziona."));
    }

    public void addBook(@Validated AdminBookCreateDTO adminBookCreateDTO) {
        Book book = new Book();

        populateBookFromDto(book,
                adminBookCreateDTO.getTitle(),
                adminBookCreateDTO.getAuthor(),
                adminBookCreateDTO.getPublisher(),
                adminBookCreateDTO.getIsbn(),
                adminBookCreateDTO.getPublicationYear(),
                adminBookCreateDTO.getGenre(),
                adminBookCreateDTO.getPageCount(),
                adminBookCreateDTO.getLanguage(),
                adminBookCreateDTO.getDescription(),
                adminBookCreateDTO.getImagePath());
    }

    public void updateBook(Long id, @Validated AdminBookUpdateDTO adminBookUpdateDTO) {
        Book book = findBookById(id);

        String imagePath = adminBookUpdateDTO.getImagePath() == null ? book.getImagePath() : adminBookUpdateDTO.getImagePath();

        populateBookFromDto(book,
                adminBookUpdateDTO.getTitle(),
                adminBookUpdateDTO.getAuthor(),
                adminBookUpdateDTO.getPublisher(),
                adminBookUpdateDTO.getIsbn(),
                adminBookUpdateDTO.getPublicationYear(),
                adminBookUpdateDTO.getGenre(),
                adminBookUpdateDTO.getPageCount(),
                adminBookUpdateDTO.getLanguage(),
                adminBookUpdateDTO.getDescription(),
                imagePath);
    }

    private void populateBookFromDto(Book book, String title, String author, String publisher, String isbn, int publicationYear, Genre genre, int pageCount, Language language, String description, String imagePath) {
        book.setTitle(title);
        book.setAuthor(author);
        book.setPublisher(publisher);
        book.setIsbn(isbn);
        book.setPublicationYear(publicationYear);
        book.setGenre(genre);
        book.setPageCount(pageCount);
        book.setLanguage(language);
        book.setDescription(description);
        book.setImagePath(imagePath);

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

    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }
}
