package com.example.bookhub.service;

import com.example.bookhub.model.dto.ReviewCreateDTO;
import com.example.bookhub.model.entity.Book;
import com.example.bookhub.model.entity.Review;
import com.example.bookhub.model.entity.User;
import com.example.bookhub.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookService bookService;
    private final UserService userService;

    public Review findReviewById(Long id) {
        Optional<Review> review = reviewRepository.findById(id);
        return review.orElseThrow(() -> new IllegalArgumentException("Recenzja o podanym id nie istnieje: " + id));
    }

    public void addReview(@Validated ReviewCreateDTO reviewCreateDTO, Long bookId) {
        Book book = bookService.findBookById(bookId);
        User currentUser = userService.getCurrentUser();

        Review review = new Review();

        review.setId(null);
        review.setRating(reviewCreateDTO.getRating());
        review.setContent(reviewCreateDTO.getContent());
        review.setBook(book);
        review.setUser(currentUser);

        reviewRepository.save(review);
        bookService.updateBookRatings(book);
    }

    public void deleteReviewById(Long id) {
        reviewRepository.deleteById(id);
    }
}
