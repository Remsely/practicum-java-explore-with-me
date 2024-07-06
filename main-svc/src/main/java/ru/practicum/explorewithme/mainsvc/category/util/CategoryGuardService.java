package ru.practicum.explorewithme.mainsvc.category.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.mainsvc.category.entity.Category;
import ru.practicum.explorewithme.mainsvc.category.repository.CategoryRepository;
import ru.practicum.explorewithme.mainsvc.common.utils.exceptions.EntityByIdExistenceGuard;
import ru.practicum.explorewithme.mainsvc.exception.AccessRightsException;
import ru.practicum.explorewithme.mainsvc.exception.AlreadyExistsException;
import ru.practicum.explorewithme.mainsvc.exception.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryGuardService implements EntityByIdExistenceGuard<Category, Long> {
    private final CategoryRepository categoryRepository;

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Category with id = " + id + " not found.")
        );
    }

    @Override
    public void checkExistenceById(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category with id = " + id + " not found.");
        }
    }

    public void checkNameUniqueness(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new AlreadyExistsException("Category with name = " + name + " already exists.");
        }
    }

    public void checkNotContainEvents(long categoryId) {
        if (categoryRepository.existsEventByCategoryId(categoryId)) {
            throw new AccessRightsException("Category with id = " + categoryId + " has events.");
        }
    }
}
