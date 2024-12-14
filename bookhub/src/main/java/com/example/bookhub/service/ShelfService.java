package com.example.bookhub.service;

import com.example.bookhub.enums.ShelfType;
import com.example.bookhub.exception.EntityNotFoundException;
import com.example.bookhub.model.dto.ShelfCreateDTO;
import com.example.bookhub.model.dto.ShelfUpdateDTO;
import com.example.bookhub.model.entity.Shelf;
import com.example.bookhub.model.entity.User;
import com.example.bookhub.repository.ShelfRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShelfService {
    private final ShelfRepository shelfRepository;

    public void initializeUserShelves(User user) {
        Shelf wantToRead = new Shelf();
        wantToRead.setName("Do przeczytania");
        wantToRead.setType(ShelfType.WANT_TO_READ);
        wantToRead.setUser(user);

        Shelf read = new Shelf();
        read.setName("Przeczytane");
        read.setType(ShelfType.READ);
        read.setUser(user);

        shelfRepository.save(wantToRead);
        shelfRepository.save(read);
    }

    public Shelf findShelfById(Long id) {
        return shelfRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Półka o podanym ID nie została znaleziona."));
    }

    public Shelf findShelfByNameAndUser(String name, User user) {
        return shelfRepository.findByNameAndUser(name, user);
    }

    public List<Shelf> findShelvesForUser(User user) {
        return shelfRepository.findByUser(user);
    }

    @Transactional
    public void saveShelf(Shelf shelf) {
        shelfRepository.save(shelf);
    }

    public void addShelf(@Validated ShelfCreateDTO shelfCreateDTO, User user) {
        Shelf shelf = new Shelf();

        shelf.setName(shelfCreateDTO.getName());
        shelf.setUser(user);

        shelfRepository.save(shelf);
    }

    public void updateShelf(Long id, @Validated ShelfUpdateDTO shelfUpdateDTO) {
        Shelf shelf = findShelfById(id);

        if(!shelf.getName().equals(shelfUpdateDTO.getName())) {
            shelf.setName(shelfUpdateDTO.getName());
            shelfRepository.save(shelf);
        }
    }

    public void deleteShelf(Long id) {
        shelfRepository.deleteById(id);
    }

    @Transactional
    public void deleteUserShelves(User user) {
        shelfRepository.deleteAllByUser(user);
    }
}
