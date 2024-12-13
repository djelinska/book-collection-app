package com.example.bookhub.model.dto;

import com.example.bookhub.enums.Genre;
import com.example.bookhub.enums.Language;
import com.example.bookhub.validator.PublicationYearConstraint;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BookCreateDTO {
    @NotBlank(message = "Tytuł książki jest wymagany")
    @Size(max = 200, message = "Tytuł książki nie może mieć więcej niż 200 znaków")
    private String title;

    @NotBlank(message = "Autor książki jest wymagany")
    @Size(max = 100, message = "Nazwa autora nie może mieć więcej niż 100 znaków")
    private String author;

    @NotBlank(message = "Wydawnictwo książki jest wymagane")
    @Size(max = 100, message = "Nazwa wydawcy nie może mieć więcej niż 100 znaków")
    private String publisher;

    @Size(min = 13, max = 13, message = "ISBN musi zawierać 13 znaków")
    private String isbn;

    @Min(value = 1000, message = "Rok publikacji musi być co najmniej 1000")
    @PublicationYearConstraint
    private int publicationYear;

    private Genre genre;

    @Min(value = 1, message = "Liczba stron musi wynosić co najmniej 1")
    @Max(value = 10000, message = "Liczba stron nie może być większa niż 10000")
    private int pageCount;

    private Language language;

    @NotBlank(message = "Opis książki jest wymagany")
    @Size(max = 2000, message = "Opis książki nie może mieć więcej niż 2000 znaków")
    private String description;

    private MultipartFile image;

    private String imagePath;
}
