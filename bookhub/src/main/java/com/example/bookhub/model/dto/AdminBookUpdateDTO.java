package com.example.bookhub.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminBookUpdateDTO extends AdminBookCreateDTO {
    private Long id;
}
