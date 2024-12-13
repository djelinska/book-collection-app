package com.example.bookhub.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShelfBackupDTO {
    private String name;
    private List<Long> bookIds;
}
