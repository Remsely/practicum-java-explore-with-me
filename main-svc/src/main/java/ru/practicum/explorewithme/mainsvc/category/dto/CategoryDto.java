package ru.practicum.explorewithme.mainsvc.category.dto;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class CategoryDto {
    private Long id;

    @NotBlank(message = "Category name must not be blank.")
    @Length(max = 50, message = "Category name must be less than 50 characters.")
    private String name;
}
