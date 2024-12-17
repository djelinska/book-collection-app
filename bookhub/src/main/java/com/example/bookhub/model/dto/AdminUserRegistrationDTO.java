package com.example.bookhub.model.dto;

import com.example.bookhub.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminUserRegistrationDTO extends UserRegistrationDTO {
    @NotNull(message = "Rola użytkownika jest wymagana")
    private Role role;
}
