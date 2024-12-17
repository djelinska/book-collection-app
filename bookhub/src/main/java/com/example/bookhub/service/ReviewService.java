package com.example.bookhub.service;

import com.example.bookhub.exception.EntityNotFoundException;
import com.example.bookhub.model.dto.AdminReviewCreateDTO;
import com.example.bookhub.model.dto.AdminReviewUpdateDTO;
import com.example.bookhub.model.dto.ReviewCreateDTO;
import com.example.bookhub.model.dto.ShelfUpdateDTO;
import com.example.bookhub.model.entity.Book;
import com.example.bookhub.model.entity.Review;
import com.example.bookhub.model.entity.Shelf;
import com.example.bookhub.model.entity.User;
import com.example.bookhub.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookService bookService;
    private final UserService userService;

    public Page<Review> searchReviews(String query, Pageable pageable) {
        if ((query == null || query.trim().isEmpty())) {
            return reviewRepository.findAll(pageable);
        }
        return reviewRepository.findByContentContainingIgnoreCase(query, pageable);
    }


    public Review findReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recenzja o podanym ID nie została znaleziona."));
    }

    public void addReview(@Validated ReviewCreateDTO reviewCreateDTO, Long bookId) {
        User currentUser = userService.getCurrentUser();
        Book book = bookService.findBookById(bookId);

        saveReview(reviewCreateDTO.getRating(), reviewCreateDTO.getContent(), book, currentUser);
    }

    public void addReviewByAdmin(@Validated AdminReviewCreateDTO adminReviewCreateDTO) {
        User user = userService.findUserById(adminReviewCreateDTO.getUserId());
        Book book = bookService.findBookById(adminReviewCreateDTO.getBookId());

        saveReview(adminReviewCreateDTO.getRating(), adminReviewCreateDTO.getContent(), book, user);
    }

    public void updateReviewByAdmin(Long id, @Validated AdminReviewUpdateDTO adminReviewUpdateDTO) {
        Review review = findReviewById(id);
        User user = userService.findUserById(adminReviewUpdateDTO.getUserId());
        Book book = bookService.findBookById(adminReviewUpdateDTO.getBookId());

        review.setRating(adminReviewUpdateDTO.getRating());
        review.setContent(adminReviewUpdateDTO.getContent());
        review.setBook(book);
        review.setUser(user);

        reviewRepository.save(review);
        bookService.updateBookRatings(book);
    }

    private void saveReview(int rating, String content, Book book, User user) {
        Review review = new Review();

        review.setId(null);
        review.setRating(rating);
        review.setContent(content);
        review.setBook(book);
        review.setUser(user);

        reviewRepository.save(review);
        bookService.updateBookRatings(book);
    }

    public void deleteReviewById(Long id) {
        reviewRepository.deleteById(id);
    }
}
