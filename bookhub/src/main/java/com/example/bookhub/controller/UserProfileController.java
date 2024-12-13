package com.example.bookhub.controller;

import com.example.bookhub.model.dto.ShelfBackupDTO;
import com.example.bookhub.model.dto.UserBackupDTO;
import com.example.bookhub.model.dto.UserUpdateDTO;
import com.example.bookhub.model.entity.Book;
import com.example.bookhub.model.entity.Shelf;
import com.example.bookhub.model.entity.User;
import com.example.bookhub.service.BookService;
import com.example.bookhub.service.ShelfBookFacade;
import com.example.bookhub.service.ShelfService;
import com.example.bookhub.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class UserProfileController {
    private final UserService userService;
    private final BookService bookService;
    private final ShelfService shelfService;
    private final ObjectMapper jacksonObjectMapper;
    private final ShelfBookFacade shelfBookFacade;

    @GetMapping
    public String showProfile(Model model) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("user", currentUser);
        return "profile/profile";
    }

    @GetMapping("/edit")
    public String showEditProfileForm(Model model) {
        model.addAttribute("user", new UserUpdateDTO());
        return "profile/profile-edit";
    }

    @PostMapping("/edit")
    public String updatePassword(@Valid @ModelAttribute("user") UserUpdateDTO userUpdateDTO, BindingResult result, Model model) {
        return userService.updateUserProfile(userUpdateDTO, result);
    }

    @PostMapping("/delete")
    public String deleteAccount() {
        User user = userService.getCurrentUser();

        userService.deleteUser(user);

        return "redirect:/profile";
    }

    @GetMapping("/profile/backup")
    public ResponseEntity<Resource> createBackup() {
        try {
            User user = userService.getCurrentUser();

            List<ShelfBackupDTO> shelvesBackup = new ArrayList<>();
            for (Shelf shelf : user.getShelves()) {
                ShelfBackupDTO shelfBackupDTO = new ShelfBackupDTO();
                shelfBackupDTO.setName(shelf.getName());

                List<Long> bookIds = shelf.getBooks().stream()
                        .map(Book::getId)
                        .collect(Collectors.toList());
                shelfBackupDTO.setBookIds(bookIds);

                shelvesBackup.add(shelfBackupDTO);
            }

            UserBackupDTO userBackupDTO = new UserBackupDTO();
            userBackupDTO.setUsername(user.getUsername());
            userBackupDTO.setShelves(shelvesBackup);

            byte[] backupData = jacksonObjectMapper.writeValueAsBytes(userBackupDTO);

            Resource resource = new ByteArrayResource(backupData);
            String filename = "backup_" + user.getUsername() + ".json";

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/profile/import")
    public String importBackup(@RequestParam("file") MultipartFile file) {
        try {
            String json = new String(file.getBytes(), StandardCharsets.UTF_8);
            UserBackupDTO userBackupDTO = jacksonObjectMapper.readValue(json, UserBackupDTO.class);

            User user = userService.getCurrentUser();

            shelfService.deleteUserShelves(user);
            shelfService.initializeUserShelves(user);

            for (ShelfBackupDTO shelfBackupDTO : userBackupDTO.getShelves()) {
                Shelf shelf = shelfService.findShelfByNameAndUser(shelfBackupDTO.getName(), user);
                if (shelf == null) {
                    shelf = new Shelf();
                    shelf.setName(shelfBackupDTO.getName());
                    shelf.setUser(user);
                    shelfService.saveShelf(shelf);
                }

                for (Long bookId : shelfBackupDTO.getBookIds()) {
                    Book book = bookService.findBookById(bookId);
                    if (book != null) {
                        shelfBookFacade.addBookToShelf(bookId, shelf.getId());
                    }
                }
            }

            return "redirect:/shelves";
        } catch (Exception e) {
            return "redirect:/profile";
        }
    }
}
