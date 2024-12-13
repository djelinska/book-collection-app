package com.example.bookhub.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBackupDTO {
    private String username;
    private List<ShelfBackupDTO> shelves;
}
