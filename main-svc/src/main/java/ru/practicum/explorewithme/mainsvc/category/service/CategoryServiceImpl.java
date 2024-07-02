package ru.practicum.explorewithme.mainsvc.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.mainsvc.category.dto.CategoryDto;
import ru.practicum.explorewithme.mainsvc.category.entity.Category;
import ru.practicum.explorewithme.mainsvc.category.mapper.CategoryMapper;
import ru.practicum.explorewithme.mainsvc.category.repository.CategoryRepository;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.common.utils.pageable.PageableUtility;
import ru.practicum.explorewithme.mainsvc.common.utils.repositories.CategoryRepositoryHelper;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepositoryHelper categoryRepositoryHelper;
    private final CategoryRepository categoryRepository;
    private final PageableUtility pageableUtility;

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        categoryRepositoryHelper.checkNameUniqueness(categoryDto.getName());

        Category category = categoryMapper.toEntity(categoryDto);
        Category savedCategory = categoryRepository.save(category);

        CategoryDto dto = categoryMapper.toDto(savedCategory);
        log.info("Category has been saved : {}", dto);
        return dto;
    }

    @Override
    public void deleteCategory(Long catId) {
        categoryRepositoryHelper.checkExistenceById(catId);
        categoryRepository.deleteById(catId);
        log.info("Category with id = {} has been deleted.", catId);
    }

    @Override
    public CategoryDto patchCategory(Long catId, CategoryDto categoryDto) {
        Category category = categoryRepositoryHelper.findById(catId);

        String name = categoryDto.getName();
        if (!name.equals(category.getName())) {
            categoryRepositoryHelper.checkNameUniqueness(name);
            category.setName(name);
        }

        Category savedCategory = categoryRepository.save(category);
        CategoryDto dto = categoryMapper.toDto(savedCategory);

        log.info("Category with id = {} has been patched. Category : {}", catId, dto);
        return dto;
    }

    @Override
    public CategoryDto getCategory(Long catId) {
        Category category = categoryRepositoryHelper.findById(catId);
        CategoryDto dto = categoryMapper.toDto(category);
        log.info("Category with id = {} has been found. Category : {}", catId, dto);
        return dto;
    }

    @Override
    public List<CategoryDto> getCategories(PaginationRequest request) {
        Pageable pageable = pageableUtility.toPageable(request);
        List<Category> categories = categoryRepository.findAll(pageable).toList();

        List<CategoryDto> dtos = categoryMapper.toDtoList(categories);
        log.info("Categories have been found. List size : {}", dtos.size());
        return dtos;
    }
}
