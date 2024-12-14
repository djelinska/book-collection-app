package com.example.bookhub.controller;

import com.example.bookhub.model.dto.UserRegistrationDTO;
import com.example.bookhub.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String handleRegistration(@Valid @ModelAttribute("user") UserRegistrationDTO userRegistrationDTO, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/register";
        }

        if (userService.usernameExists(userRegistrationDTO.getUsername())) {
            result.rejectValue("username", "error.username", "Nazwa użytkownika już istnieje");
            return "auth/register";
        }

        if (!userRegistrationDTO.getPassword().equals(userRegistrationDTO.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Hasła nie są takie same");
            return "auth/register";
        }

        userService.registerUser(userRegistrationDTO);

        redirectAttributes.addFlashAttribute("successMessage", "Rejestracja zakończona sukcesem! Możesz się teraz zalogować.");

        return "redirect:/auth/login";
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Nieprawidłowa nazwa użytkownika lub hasło");
        }
        return "auth/login";
    }
}
