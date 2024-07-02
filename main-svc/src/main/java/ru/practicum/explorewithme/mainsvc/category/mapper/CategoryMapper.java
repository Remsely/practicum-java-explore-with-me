package ru.practicum.explorewithme.mainsvc.category.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.mainsvc.category.dto.CategoryDto;
import ru.practicum.explorewithme.mainsvc.category.entity.Category;

@Component
public class CategoryMapper {
    public Category toEntity(CategoryDto categoryDto) {
        return Category.builder()
                .name(categoryDto.getName())
                .build();
    }

    public CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
