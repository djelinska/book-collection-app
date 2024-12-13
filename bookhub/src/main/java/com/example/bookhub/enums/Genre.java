package com.example.bookhub.enums;

import lombok.Getter;

@Getter
public enum Genre {
    FICTION("Fikcja"),
    NON_FICTION("Literatura faktu"),
    MYSTERY("Kryminał"),
    FANTASY("Fantastyka"),
    SCI_FI("Science Fiction"),
    ROMANCE("Romans"),
    HORROR("Horror"),
    DYSTOPIA("Dystopia"),
    BIOGRAPHY("Biografia"),
    HISTORY("Historia"),
    CLASSIC("Klasyka");

    private final String displayName;

    Genre(String displayName) {
        this.displayName = displayName;
    }
}
