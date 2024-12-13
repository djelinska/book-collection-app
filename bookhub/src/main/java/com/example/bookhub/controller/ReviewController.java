package com.example.bookhub.controller;

import com.example.bookhub.model.entity.Book;
import com.example.bookhub.model.entity.Review;
import com.example.bookhub.model.entity.User;
import com.example.bookhub.service.BookService;
import com.example.bookhub.service.ReviewService;
import com.example.bookhub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;
    private final BookService bookService;

    @PostMapping("/{id}/delete")
    public String deleteReview(@PathVariable Long id) {
        Review review = reviewService.findReviewById(id);
        User currentUser = userService.getCurrentUser();
        Book book = review.getBook();

        if (!review.getUser().equals(currentUser)) {
            return "redirect:/books/" + book.getId();
        }

        reviewService.deleteReviewById(id);
        bookService.updateBookRatings(book);

        return "redirect:/books/" + book.getId();
    }
}
