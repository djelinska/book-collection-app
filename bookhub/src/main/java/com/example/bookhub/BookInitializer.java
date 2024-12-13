package com.example.bookhub;

import com.example.bookhub.model.entity.Book;
import com.example.bookhub.repository.BookRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class BookInitializer {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    @Qualifier("xmlApplicationContext")
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        String[] bookBeanNames = applicationContext.getBeanNamesForType(Book.class);
        Arrays.stream(bookBeanNames)
                .map(name -> (Book) applicationContext.getBean(name))
                .forEach(bookRepository::save);
    }
}
