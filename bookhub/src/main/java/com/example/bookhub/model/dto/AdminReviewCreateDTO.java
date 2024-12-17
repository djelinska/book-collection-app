package com.example.bookhub.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminReviewCreateDTO {
    @NotNull(message = "Ocena jest wymagana")
    @Min(value = 1, message = "Ocena musi być co najmniej 1")
    @Max(value = 5, message = "Ocena nie może być większa niż 5")
    private int rating;

    @NotBlank(message = "Treść recenzji jest wymagana")
    private String content;

    @NotNull(message = "Książka jest wymagana")
    private Long bookId;

    @NotNull(message = "Użytkownik jest wymagany")
    private Long userId;
}
