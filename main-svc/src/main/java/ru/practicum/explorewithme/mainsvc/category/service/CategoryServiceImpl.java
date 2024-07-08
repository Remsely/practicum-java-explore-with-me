package ru.practicum.explorewithme.mainsvc.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.mainsvc.category.dto.CategoryDto;
import ru.practicum.explorewithme.mainsvc.category.entity.Category;
import ru.practicum.explorewithme.mainsvc.category.mapper.CategoryMapper;
import ru.practicum.explorewithme.mainsvc.category.repository.CategoryRepository;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;
import ru.practicum.explorewithme.mainsvc.common.utils.pageable.PageableUtility;
import ru.practicum.explorewithme.mainsvc.exception.AccessRightsException;
import ru.practicum.explorewithme.mainsvc.exception.AlreadyExistsException;
import ru.practicum.explorewithme.mainsvc.exception.EntityNotFoundException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private final PageableUtility pageableUtility;

    @Transactional
    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        String name = categoryDto.getName();
        if (categoryRepository.existsByName(name)) {
            throw new AlreadyExistsException("Category with name = " + name + " already exists.");
        }

        Category category = categoryMapper.toEntity(categoryDto);
        Category savedCategory = categoryRepository.save(category);

        CategoryDto dto = categoryMapper.toDto(savedCategory);
        log.info("Category has been saved : {}", dto);
        return dto;
    }

    @Transactional
    @Override
    public void deleteCategory(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new EntityNotFoundException("Category with id = " + catId + " not found.");
        }
        if (categoryRepository.existsEventByCategoryId(catId)) {
            throw new AccessRightsException("Category with id = " + catId + " has events.");
        }
        categoryRepository.deleteById(catId);
        log.info("Category with id = {} has been deleted.", catId);
    }

    @Transactional
    @Override
    public CategoryDto patchCategory(Long catId, CategoryDto categoryDto) {
        Category category = this.findCategoryById(catId);

        String name = categoryDto.getName();
        if (!name.equals(category.getName())) {
            if (categoryRepository.existsByName(name)) {
                throw new AlreadyExistsException("Category with name = " + name + " already exists.");
            }
            category.setName(name);
        }

        Category savedCategory = categoryRepository.save(category);
        CategoryDto dto = categoryMapper.toDto(savedCategory);

        log.info("Category with id = {} has been patched. Category : {}", catId, dto);
        return dto;
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryDto getCategoryById(Long catId) {
        Category category = this.findCategoryById(catId);
        CategoryDto dto = categoryMapper.toDto(category);
        log.info("Category with id = {} has been found. Category : {}", catId, dto);
        return dto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDto> getCategories(PaginationRequest request) {
        Pageable pageable = pageableUtility.toPageable(request);
        List<Category> categories = categoryRepository.findAll(pageable).toList();

        List<CategoryDto> dtos = categoryMapper.toDtoList(categories);
        log.info("Categories have been found. List size : {}", dtos.size());
        return dtos;
    }

    @Transactional(readOnly = true)
    @Override
    public Category findCategoryById(long id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Category with id = " + id + " not found.")
        );
    }
}
