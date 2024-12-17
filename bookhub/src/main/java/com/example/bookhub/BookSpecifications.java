package com.example.bookhub;

import com.example.bookhub.enums.Genre;
import com.example.bookhub.enums.Language;
import com.example.bookhub.model.entity.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecifications {
    public static Specification<Book> hasQuery(String query) {
        return (root, queryCriteria, builder) ->
                (query == null || query.isEmpty())
                        ? builder.conjunction()
                        : builder.or(
                        builder.like(builder.lower(root.get("title")), "%" + query.toLowerCase() + "%"),
                        builder.like(builder.lower(root.get("author")), "%" + query.toLowerCase() + "%")
                );
    }

    public static Specification<Book> hasGenre(Genre genre) {
        return (root, queryCriteria, builder) ->
                genre == null ? builder.conjunction() : builder.equal(root.get("genre"), genre);
    }

    public static Specification<Book> hasLanguage(Language language) {
        return (root, queryCriteria, builder) ->
                language == null ? builder.conjunction() : builder.equal(root.get("language"), language);
    }
}
