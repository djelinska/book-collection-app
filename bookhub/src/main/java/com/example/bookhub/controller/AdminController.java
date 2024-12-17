package com.example.bookhub.controller;

import com.example.bookhub.enums.Genre;
import com.example.bookhub.enums.Language;
import com.example.bookhub.enums.Role;
import com.example.bookhub.model.dto.AdminBookCreateDTO;
import com.example.bookhub.model.dto.AdminBookUpdateDTO;
import com.example.bookhub.model.dto.AdminUserRegistrationDTO;
import com.example.bookhub.model.dto.AdminUserUpdateDTO;
import com.example.bookhub.model.entity.Book;
import com.example.bookhub.model.entity.User;
import com.example.bookhub.service.BookService;
import com.example.bookhub.service.FileService;
import com.example.bookhub.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {
    private final UserService userService;
    private final BookService bookService;
    private final FileService fileService;
    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;

    @GetMapping
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String showUsers(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userService.searchUsers(query, pageable);

        User user = userService.getCurrentUser();

        model.addAttribute("active", "users");
        model.addAttribute("users", users);
        model.addAttribute("query", query);
        model.addAttribute("loggedUserId", user.getId());

        return "admin/list-users";
    }

    @GetMapping("/users/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new AdminUserRegistrationDTO());
        model.addAttribute("roles", Role.values());
        return "admin/add-user";
    }

    @PostMapping("/users/add")
    public String addUser(@Valid @ModelAttribute("user") AdminUserRegistrationDTO adminUserRegistrationDTO, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", Role.values());
            return "admin/add-user";
        }

        userService.registerUserByAdmin(adminUserRegistrationDTO);

        return "redirect:/admin/users";
    }

    @GetMapping("/users/{id}/edit")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userService.findUserById(id);

        AdminUserUpdateDTO adminUserUpdateDTO = new AdminUserUpdateDTO();
        adminUserUpdateDTO.setId(user.getId());
        adminUserUpdateDTO.setUsername(user.getUsername());
        adminUserUpdateDTO.setRole(user.getRole());

        model.addAttribute("user", adminUserUpdateDTO);
        model.addAttribute("roles", Role.values());
        return "admin/edit-user";
    }

    @PostMapping("/users/{id}/edit")
    public String editUser(@PathVariable Long id, @Valid @ModelAttribute("user") AdminUserUpdateDTO adminUserUpdateDTO,
                             BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", Role.values());
            return "admin/edit-user";
        }

        try {
            if (!adminUserUpdateDTO.getRole().equals(Role.ROLE_ADMIN) && userService.isLastAdmin(id)) {
                result.rejectValue("role", "error.user", "Nie można zmienić roli administratora, ponieważ w systemie musi pozostać co najmniej jeden administrator");
                model.addAttribute("roles", Role.values());
                return "admin/edit-user";
            }

            userService.updateUser(id, adminUserUpdateDTO);

            User loggedInUser = userService.getCurrentUser();
            if (loggedInUser.getId().equals(id)) {
                SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
                logoutHandler.logout(httpServletRequest, httpServletResponse, SecurityContextHolder.getContext().getAuthentication());
                redirectAttributes.addFlashAttribute("successMessage", "Dane użytkownika zostały pomyślnie zaktualizowane. Zostałeś wylogowany.");
                return "redirect:/auth/login";
            }

            redirectAttributes.addFlashAttribute("successMessage", "Dane użytkownika zostały pomyślnie zaktualizowane.");
            return "redirect:/admin/users";

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentUser();

        if (user.getId().equals(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nie możesz usunąć swojego konta.");
            return "redirect:/admin/users";
        }

        userService.deleteUserById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Użytkownik został usunięty.");
        return "redirect:/admin/users";
    }

    @GetMapping("/books")
    public String showBooks(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookService.searchBooks(query, pageable);

        model.addAttribute("active", "books");
        model.addAttribute("books", books);
        model.addAttribute("query", query);

        return "admin/list-books";
    }

    @GetMapping("/books/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new AdminBookCreateDTO());
        model.addAttribute("genres", Genre.values());
        model.addAttribute("languages", Language.values());
        return "admin/add-book";
    }

    @PostMapping("/books/add")
    public String addBook(@Valid @ModelAttribute("book") AdminBookCreateDTO adminBookCreateDTO,
                          BindingResult result, Model model,
                          @RequestParam("image") MultipartFile imageFile) {

        if(imageFile == null || imageFile.isEmpty()) {
            result.rejectValue("image", "error.image", "Okładka książki jest wymagana");
        } else if (fileService.isImageFileValid(imageFile, result)) {
            try {
                String path = fileService.saveImage(imageFile, adminBookCreateDTO.getTitle());
                adminBookCreateDTO.setImagePath(path);
            } catch (IOException e) {
                result.rejectValue("image", "error.image", "Nie udało się zapisać okładki");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("genres", Genre.values());
            model.addAttribute("languages", Language.values());
            return "admin/add-book";
        }

        bookService.addBook(adminBookCreateDTO);

        return "redirect:/admin/books";
    }

    @GetMapping("/books/{id}/edit")
    public String editBook(@PathVariable Long id, Model model) {
        Book book = bookService.findBookById(id);

        AdminBookUpdateDTO adminBookUpdateDTO = new AdminBookUpdateDTO();
        adminBookUpdateDTO.setId(book.getId());
        adminBookUpdateDTO.setTitle(book.getTitle());
        adminBookUpdateDTO.setAuthor(book.getAuthor());
        adminBookUpdateDTO.setPublisher(book.getPublisher());
        adminBookUpdateDTO.setIsbn(book.getIsbn());
        adminBookUpdateDTO.setPublicationYear(book.getPublicationYear());
        adminBookUpdateDTO.setGenre(book.getGenre());
        adminBookUpdateDTO.setPageCount(book.getPageCount());
        adminBookUpdateDTO.setLanguage(book.getLanguage());
        adminBookUpdateDTO.setDescription(book.getDescription());
        adminBookUpdateDTO.setImagePath(book.getImagePath());

        model.addAttribute("book", adminBookUpdateDTO);
        model.addAttribute("genres", Genre.values());
        model.addAttribute("languages", Language.values());

        return "admin/edit-book";
    }

    @PostMapping("/books/{id}/edit")
    public String editBook(@PathVariable Long id, @Valid @ModelAttribute("book") AdminBookUpdateDTO adminBookUpdateDTO,
                           BindingResult result, RedirectAttributes redirectAttributes, Model model, @RequestParam("image") MultipartFile imageFile) {

        if(!imageFile.isEmpty()) {
            if (fileService.isImageFileValid(imageFile, result)) {
                try {
                    String path = fileService.saveImage(imageFile, adminBookUpdateDTO.getTitle());
                    adminBookUpdateDTO.setImagePath(path);
                } catch (IOException e) {
                    result.rejectValue("image", "error.image", "Nie udało się zapisać okładki");
                }
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("genres", Genre.values());
            model.addAttribute("languages", Language.values());
            return "admin/edit-book";
        }

        bookService.updateBook(id, adminBookUpdateDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Informacje o książce zostały pomyślnie zaktualizowane.");
        return "redirect:/admin/books";
    }

    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bookService.deleteBookById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Książka została usunięta.");
        return "redirect:/admin/books";
    }
}
