package com.example.bookhub.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ShelfCreateDTO {
    @NotBlank(message = "Nazwa półki jest wymagana")
    @Size(max = 100, message = "Nazwa półki nie może mieć więcej niż 100 znaków")
    private String name;
}
