package com.example.bookhub.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminReviewUpdateDTO extends AdminReviewCreateDTO {
    private Long id;
}
