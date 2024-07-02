package ru.practicum.explorewithme.mainsvc.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.mainsvc.category.dto.CategoryDto;
import ru.practicum.explorewithme.mainsvc.category.entity.Category;
import ru.practicum.explorewithme.mainsvc.category.mapper.CategoryMapper;
import ru.practicum.explorewithme.mainsvc.category.repository.CategoryRepository;
import ru.practicum.explorewithme.mainsvc.util.repositories.RepositoryHelper;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final RepositoryHelper<Category> categoryRepositoryHelper;
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        Category category = categoryMapper.toEntity(categoryDto);
        Category savedCategory = categoryRepository.save(category);

        CategoryDto dto = categoryMapper.toDto(savedCategory);
        log.info("Category has been saved : {}", dto);
        return dto;
    }

    @Override
    public void deleteCategory(Long catId) {
        categoryRepositoryHelper.checkExistence(catId);
        categoryRepository.deleteById(catId);
        log.info("Category with id = {} has been deleted.", catId);
    }

    @Override
    public CategoryDto patchCategory(Long catId, CategoryDto categoryDto) {
        Category category = categoryRepositoryHelper.findById(catId);
        category.setName(categoryDto.getName());

        Category savedCategory = categoryRepository.save(category);
        CategoryDto dto = categoryMapper.toDto(savedCategory);

        log.info("Category with id = {} has been patched. Category : {}", catId, dto);
        return dto;
    }
}
