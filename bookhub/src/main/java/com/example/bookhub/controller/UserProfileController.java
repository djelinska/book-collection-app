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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String updatePassword(@Valid @ModelAttribute("user") UserUpdateDTO userUpdateDTO, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        return userService.updateUserProfile(userUpdateDTO, result, redirectAttributes);
    }

    @PostMapping("/delete")
    public String deleteAccount(RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentUser();
        userService.deleteUser(user);

        redirectAttributes.addFlashAttribute("successMessage", "Twoje konto zostało pomyślnie usunięte.");

        return "redirect:/auth/register";
    }

    @GetMapping("/backup")
    public ResponseEntity<Resource> createBackup(RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getCurrentUser();

            byte[] backupData = userService.createBackup(user);

            Resource resource = new ByteArrayResource(backupData);
            String filename = "backup_" + user.getUsername() + ".json";

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas tworzenia kopii zapasowej.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header(HttpHeaders.LOCATION, "/profile")
                    .build();
        }
    }

    @PostMapping("/import")
    public String importBackup(@RequestParam("file") MultipartFile file, Model model, RedirectAttributes redirectAttributes) {
        if (file == null || file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Plik nie został załączony");
            return "redirect:/profile";
        }

        String contentType = file.getContentType();
        if (!contentType.equals("application/json")) {
            redirectAttributes.addFlashAttribute("error", "Niedozwolony format pliku. Wymagany format to JSON");
            return "redirect:/profile";
        }

        try {
            String json = new String(file.getBytes(), StandardCharsets.UTF_8);
            userService.importBackup(json);

            redirectAttributes.addFlashAttribute("successMessage", "Kopia zapasowa została pomyślnie zaimportowana.");
            return "redirect:/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas importowania kopii zapasowej.");
            return "redirect:/profile";
        }
    }
}
