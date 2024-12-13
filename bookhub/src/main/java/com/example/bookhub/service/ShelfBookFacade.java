package com.example.bookhub.service;

import com.example.bookhub.model.entity.Book;
import com.example.bookhub.model.entity.Shelf;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ShelfBookFacade {
    private final BookService bookService;
    private final ShelfService shelfService;

    public void addBookToShelf(Long bookId, Long shelfId) {
        Book book = bookService.findBookById(bookId);
        Shelf shelf = shelfService.findShelfById(shelfId);

        if (!shelf.getBooks().contains(book)) {
            shelf.getBooks().add(book);
            book.getShelves().add(shelf);
            shelfService.saveShelf(shelf);
        }
    }

    public void removeBookFromShelf(Long shelfId, Long bookId) {
        Shelf shelf = shelfService.findShelfById(shelfId);
        Book book = bookService.findBookById(bookId);

        shelf.getBooks().remove(book);
        shelfService.saveShelf(shelf);
    }
}
