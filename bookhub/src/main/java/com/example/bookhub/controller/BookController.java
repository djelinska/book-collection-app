package com.example.bookhub.controller;

import com.example.bookhub.enums.Genre;
import com.example.bookhub.enums.Language;
import com.example.bookhub.model.dto.ReviewCreateDTO;
import com.example.bookhub.model.entity.Book;
import com.example.bookhub.model.entity.Review;
import com.example.bookhub.model.entity.Shelf;
import com.example.bookhub.model.entity.User;
import com.example.bookhub.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;
    private final ShelfService shelfService;
    private final UserService userService;
    private final ReviewService reviewService;
    private final FileService fileService;
    private final StatisticsService statisticsService;
    private final ShelfBookFacade shelfBookFacade;

    public void populateBookDetailsView(@PathVariable("id") Long bookId, Model model) {
        Book book = bookService.findBookById(bookId);
        Map<String, Integer> stats = statisticsService.getBookStatistics(book);
        List<Review> reviews = book.getReviews();

        model.addAttribute("book", book);
        model.addAttribute("stats", stats);
        model.addAttribute("reviews", reviews);
        model.addAttribute("numberOfReviews", reviews.size());
        model.addAttribute("averageRating", book.getAverageRating());
        model.addAttribute("numberOfRatings", book.getNumberOfRatings());
    }

    @GetMapping
    public String listBooks(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Genre genre,
            @RequestParam(required = false) Language language,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
            @RequestParam(defaultValue = "title") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection,
            Model model) {

        Sort sort = sortDirection.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Book> books = bookService.searchBooks(query, genre, language, pageable);

        model.addAttribute("books", books);
        model.addAttribute("query", query);
        model.addAttribute("genre", genre);
        model.addAttribute("language", language);
        model.addAttribute("genres", Genre.values());
        model.addAttribute("languages", Language.values());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);

        return "books/list-books";
    }

    @GetMapping("/{id}")
    public String viewBookDetails(@PathVariable Long id, Model model) {
        populateBookDetailsView(id, model);
        model.addAttribute("review", new ReviewCreateDTO());
        return "books/book-details";
    }

    @GetMapping("/{id}/add-to-shelf")
    public String showAddToShelfForm(@PathVariable Long id, Model model) {
        Book book = bookService.findBookById(id);
        User user = userService.getCurrentUser();
        List<Shelf> shelves = shelfService.findShelvesForUser(user);
        model.addAttribute("book", book);
        model.addAttribute("shelves", shelves);
        return "books/add-to-shelf";
    }

    @PostMapping("/{id}/add-to-shelf")
    public String addToShelf(@PathVariable Long id, @RequestParam Long shelfId) {
        shelfBookFacade.addBookToShelf(id, shelfId);
        return "redirect:/shelves";
    }

    @PostMapping("/{id}/add-review")
    public String addReview(@PathVariable("id") Long bookId, @Valid @ModelAttribute("review") ReviewCreateDTO reviewCreateDTO,
                            BindingResult result, Model model) {
        if (result.hasErrors()) {
            populateBookDetailsView(bookId, model);
            return "books/book-details";
        }

        reviewService.addReview(reviewCreateDTO, bookId);

        return "redirect:/books/" + bookId;
    }
}

