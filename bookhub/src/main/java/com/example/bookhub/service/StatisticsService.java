package com.example.bookhub.service;

import com.example.bookhub.enums.ShelfType;
import com.example.bookhub.model.entity.Book;
import com.example.bookhub.model.entity.Shelf;
import com.example.bookhub.model.entity.User;
import com.example.bookhub.repository.ShelfRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final UserService userService;
    private final ShelfRepository shelfRepository;

    public Map<String, Integer> getCurrentUserStats() {
        User currentUser = userService.getCurrentUser();
        List<Shelf> userShelves = shelfRepository.findByUser(currentUser);

        int booksRead = userShelves.stream()
                .filter(shelf -> shelf.getType() == ShelfType.READ)
                .mapToInt(shelf -> shelf.getBooks().size())
                .sum();

        int booksToRead = userShelves.stream()
                .filter(shelf -> shelf.getType() == ShelfType.WANT_TO_READ)
                .mapToInt(shelf -> shelf.getBooks().size())
                .sum();
        int shelvesCreated = currentUser.getShelves().size();

        return Map.of(
                "booksRead", booksRead,
                "booksToRead", booksToRead,
                "shelvesCreated", shelvesCreated
        );
    }

    public Map<String, Integer> getBookStatistics(Book book) {
        int readCount = shelfRepository.countByBooksContainingAndType(book, ShelfType.READ);
        int wantToReadCount = shelfRepository.countByBooksContainingAndType(book, ShelfType.WANT_TO_READ);
        return Map.of(
                "readCount", readCount,
                "wantToReadCount", wantToReadCount);
    }
}
