package com.example.bookhub.controller;

import com.example.bookhub.model.dto.ShelfCreateDTO;
import com.example.bookhub.model.dto.ShelfUpdateDTO;
import com.example.bookhub.model.entity.Shelf;
import com.example.bookhub.model.entity.User;
import com.example.bookhub.service.ShelfBookFacade;
import com.example.bookhub.service.ShelfService;
import com.example.bookhub.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/shelves")
public class ShelfController {
    private final ShelfService shelfService;
    private final UserService userService;
    private final ShelfBookFacade shelfBookFacade;

    @GetMapping
    public String listShelves(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("shelves", shelfService.findShelvesForUser(user));
        return "shelves/list-shelves";
    }

    @GetMapping("/{id}")
    public String viewShelfDetails(@PathVariable Long id, Model model) {
        Shelf shelf = shelfService.findShelfById(id);
        model.addAttribute("shelf", shelf);
        return "shelves/shelf-details";
    }

    @GetMapping("/add")
    public String showAddShelfForm(Model model) {
        model.addAttribute("shelf", new ShelfCreateDTO());
        return "shelves/add-shelf";
    }

    @PostMapping("/add")
    public String addShelf(@Valid @ModelAttribute("shelf") ShelfCreateDTO shelfCreateDTO, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "shelves/add-shelf";
        }

        User user = userService.getCurrentUser();
        shelfService.addShelf(shelfCreateDTO, user);

        return "redirect:/shelves";
    }

    @GetMapping("/{id}/edit")
    public String showEditShelfForm(@PathVariable Long id, Model model) {
        Shelf shelf = shelfService.findShelfById(id);

        ShelfUpdateDTO shelfUpdateDTO = new ShelfUpdateDTO();
        shelfUpdateDTO.setId(shelf.getId());
        shelfUpdateDTO.setName(shelf.getName());

        model.addAttribute("shelf", shelfUpdateDTO);
        return "shelves/edit-shelf";
    }

    @PostMapping("/{id}/edit")
    public String saveEditedShelf(@PathVariable Long id, @Valid @ModelAttribute("shelf") ShelfUpdateDTO shelfUpdateDTO, BindingResult result) {
        if (result.hasErrors()) {
            return "shelves/edit-shelf";
        }

        shelfService.updateShelf(id, shelfUpdateDTO);

        return "redirect:/shelves";
    }

    @PostMapping("/{id}/delete")
    public String deleteShelf(@PathVariable Long id) {
        shelfService.deleteShelf(id);
        return "redirect:/shelves";
    }

    @PostMapping("/{shelfId}/remove-book/{bookId}")
    public String removeBookFromShelf(@PathVariable Long shelfId, @PathVariable Long bookId) {
        shelfBookFacade.removeBookFromShelf(shelfId, bookId);
        return "redirect:/shelves/" + shelfId;
    }
}
