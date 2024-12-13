package com.example.bookhub.service;

import com.example.bookhub.enums.Role;
import com.example.bookhub.model.dto.UserRegistrationDTO;
import com.example.bookhub.model.dto.UserUpdateDTO;
import com.example.bookhub.model.entity.User;
import com.example.bookhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ShelfService shelfService;

    public String registerUser(@Validated UserRegistrationDTO userRegistrationDTO, BindingResult result) {
        if (result.hasErrors()) {
            return "auth/register";
        }

        if (userRepository.findByUsername(userRegistrationDTO.getUsername()).isPresent()) {
            result.rejectValue("username", "error.username", "Nazwa użytkownika już istnieje");
            return "auth/register";
        }

        if (!userRegistrationDTO.getPassword().equals(userRegistrationDTO.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Hasła nie są takie same");
            return "auth/register";
        }

        User user = new User();
        user.setUsername(userRegistrationDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);

        shelfService.initializeUserShelves(user);

        return "auth/login";
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Użytkownik o podanej nazwie nie istnieje: " + username));

    }

    public String updateUserProfile(@Validated UserUpdateDTO userUpdateDTO, BindingResult result) {
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
        return "redirect:/profile";
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }
}
