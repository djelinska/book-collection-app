package com.example.bookhub.model.dto;

import com.example.bookhub.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminUserUpdateDTO {
    private Long id;

    @NotBlank(message = "Nazwa użytkownika jest wymagana")
    private String username;

    @NotNull(message = "Rola użytkownika jest wymagana")
    private Role role;
}
