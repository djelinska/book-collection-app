package com.example.bookhub.enums;

import lombok.Getter;

@Getter
public enum Language {
    POLISH("Polski"),
    ENGLISH("Angielski"),
    GERMAN("Niemiecki"),
    FRENCH("Francuski"),
    SPANISH("Hiszpański"),
    ITALIAN("Włoski"),
    JAPANESE("Japoński"),
    CHINESE("Chiński");

    private final String displayName;

    Language(String displayName) {
        this.displayName = displayName;
    }
}
