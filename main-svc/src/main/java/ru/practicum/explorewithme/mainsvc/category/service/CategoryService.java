package ru.practicum.explorewithme.mainsvc.category.service;

import ru.practicum.explorewithme.mainsvc.category.dto.CategoryDto;
import ru.practicum.explorewithme.mainsvc.category.entity.Category;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(CategoryDto categoryDto);

    void deleteCategory(Long catId);

    CategoryDto patchCategory(Long catId, CategoryDto categoryDto);

    CategoryDto getCategoryById(Long catId);

    List<CategoryDto> getCategories(PaginationRequest request);

    Category findCategoryById(long id);
}
