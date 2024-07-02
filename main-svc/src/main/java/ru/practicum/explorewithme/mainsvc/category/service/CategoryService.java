package ru.practicum.explorewithme.mainsvc.category.service;

import ru.practicum.explorewithme.mainsvc.category.dto.CategoryDto;

public interface CategoryService {
    CategoryDto addCategory(CategoryDto categoryDto);

    void deleteCategory(Long catId);

    CategoryDto patchCategory(Long catId, CategoryDto categoryDto);
}
