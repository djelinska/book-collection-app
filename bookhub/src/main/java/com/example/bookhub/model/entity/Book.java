package com.example.bookhub.model.entity;

import com.example.bookhub.enums.Genre;
import com.example.bookhub.enums.Language;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String author;

    private String publisher;

    private String isbn;

    private int publicationYear;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    private int pageCount;

    @Enumerated(EnumType.STRING)
    private Language language;

    private double averageRating;

    private int numberOfRatings;

    @Lob
    private String description;

    private String imagePath;

    @ToString.Exclude
    @ManyToMany(mappedBy = "books")
    private List<Shelf> shelves = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "book")
    private List<Review> reviews;
}
