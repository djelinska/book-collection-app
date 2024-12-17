package com.example.bookhub.enums;

import lombok.Getter;

@Getter
public enum Role {
    ROLE_USER("Użytkownik"),
    ROLE_ADMIN("Administrator");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }
}
