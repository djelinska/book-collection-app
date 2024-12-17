package com.example.bookhub.service;

import com.example.bookhub.enums.Role;
import com.example.bookhub.exception.EntityNotFoundException;
import com.example.bookhub.model.dto.*;
import com.example.bookhub.model.entity.Book;
import com.example.bookhub.model.entity.Shelf;
import com.example.bookhub.model.entity.User;
import com.example.bookhub.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ShelfService shelfService;
    private final BookService bookService;
    private final ShelfBookFacade shelfBookFacade;
    private final ObjectMapper jacksonObjectMapper;

    public void registerUser(@Validated UserRegistrationDTO userRegistrationDTO) {
        User user = new User();
        user.setUsername(userRegistrationDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        user.setRole(Role.ROLE_USER);

        userRepository.save(user);
        shelfService.initializeUserShelves(user);
    }

    public void registerUserByAdmin(AdminUserRegistrationDTO adminUserRegistrationDTO) {
        User user = new User();
        user.setUsername(adminUserRegistrationDTO.getUsername());
        user.setPassword(passwordEncoder.encode(adminUserRegistrationDTO.getPassword()));
        user.setRole(adminUserRegistrationDTO.getRole() != null ? adminUserRegistrationDTO.getRole() : Role.ROLE_USER);

        userRepository.save(user);
        shelfService.initializeUserShelves(user);
    }

    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public Page<User> searchUsers(String query, Pageable pageable) {
        if ((query == null || query.trim().isEmpty())) {
            return userRepository.findAll(pageable);
        }
        return userRepository.findByUsernameContainingIgnoreCase(query, pageable);
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Użytkownik o podanej nazwie nie istnieje: " + username));
    }

    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Użytkownik o podanym ID nie został znaleziony."));
    }

    public void updateUser(Long id, AdminUserUpdateDTO adminUserUpdateDTO) {
        User user = findUserById(id);

        user.setUsername(adminUserUpdateDTO.getUsername());
        user.setRole(adminUserUpdateDTO.getRole());

        userRepository.save(user);
    }

    public boolean isLastAdmin(Long userId) {
        long adminCount = userRepository.countByRole(Role.ROLE_ADMIN);

        return adminCount <= 1 && findUserById(userId).getRole().equals(Role.ROLE_ADMIN);
    }

    public String updateUserProfile(@Validated UserUpdateDTO userUpdateDTO, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "profile/profile-edit";
        }

        User currentUser = getCurrentUser();

        if (!passwordEncoder.matches(userUpdateDTO.getCurrentPassword(), currentUser.getPassword())) {
            result.rejectValue("currentPassword", "error.user", "Nieprawidłowe aktualne hasło");
            return "profile/profile-edit";
        }

        if (!userUpdateDTO.getNewPassword().equals(userUpdateDTO.getConfirmNewPassword())) {
            result.rejectValue("confirmNewPassword", "error.user", "Nowe hasła nie są takie same");
            return "profile/profile-edit";
        }

        currentUser.setPassword(passwordEncoder.encode(userUpdateDTO.getNewPassword()));
        userRepository.save(currentUser);

        redirectAttributes.addFlashAttribute("successMessage", "Profil został zaktualizowany pomyślnie.");

        return "redirect:/profile";
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public byte[] createBackup(User user) throws IOException {
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

        return jacksonObjectMapper.writeValueAsBytes(userBackupDTO);
    }

    @Transactional
    public void importBackup(String json) throws Exception {
        UserBackupDTO userBackupDTO = jacksonObjectMapper.readValue(json, UserBackupDTO.class);

        User user = getCurrentUser();

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
    }
}
