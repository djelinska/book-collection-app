package com.example.bookhub.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateDTO {
    @NotBlank(message = "Aktualne hasło jest wymagane")
    private String currentPassword;

    @NotBlank(message = "Nowe hasło jest wymagane")
    private String newPassword;

    @NotBlank(message = "Potwierdzenie nowego hasła jest wymagane")
    private String confirmNewPassword;
}
