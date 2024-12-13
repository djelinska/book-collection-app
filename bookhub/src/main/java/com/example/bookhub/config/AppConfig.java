package com.example.bookhub.config;

import com.example.bookhub.enums.Role;
import com.example.bookhub.model.entity.User;
import com.example.bookhub.repository.BookRepository;
import com.example.bookhub.repository.UserRepository;
import com.example.bookhub.service.FileService;
import com.example.bookhub.service.ShelfService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@AllArgsConstructor
public class AppConfig {
    private final FileService fileService;

    @Bean
    public ClassPathXmlApplicationContext xmlApplicationContext() {
        return new ClassPathXmlApplicationContext("books.xml");
    }

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder, ShelfService shelfService, BookRepository bookRepository) {
        return args -> {
            fileService.cleanUploadsFolder();

            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRole(Role.ROLE_ADMIN);
                userRepository.save(admin);
            }
            if (!userRepository.existsByUsername("user")) {
                User user = new User();
                user.setUsername("user");
                user.setPassword(passwordEncoder.encode("1234"));
                user.setRole(Role.ROLE_USER);
                userRepository.save(user);
                shelfService.initializeUserShelves(user);
            }
        };
    }
}
