package com.example.bookhub;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class FileNameGenerator {
    public String generateUniqueFileName(String title) {
        String sanitizedTitle = title.replaceAll("\\s+", "_");
        return "book_" + sanitizedTitle + "_" + System.currentTimeMillis() + ".jpg";
    }
}
