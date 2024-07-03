package ru.practicum.explorewithme.mainsvc.common.utils.exceptions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.mainsvc.category.entity.Category;
import ru.practicum.explorewithme.mainsvc.category.repository.CategoryRepository;
import ru.practicum.explorewithme.mainsvc.exception.AlreadyExistsException;
import ru.practicum.explorewithme.mainsvc.exception.EntityNotFoundException;
import ru.practicum.explorewithme.mainsvc.exception.dto.ErrorResponseDto;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryExceptionThrower implements ByIdExceptionThrower<Category, Long> {
    private final CategoryRepository categoryRepository;

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                ErrorResponseDto.builder()
                        .status(HttpStatus.NOT_FOUND.toString())
                        .reason("Category not found.")
                        .message("Category with id = " + id + " not found.")
                        .timestamp(LocalDateTime.now())
                        .build()
        ));
    }

    @Override
    public void checkExistenceById(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    ErrorResponseDto.builder()
                            .status(HttpStatus.NOT_FOUND.toString())
                            .reason("Category not found.")
                            .message("Category with id = " + id + " not found.")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    public void checkNameUniqueness(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new AlreadyExistsException(
                    ErrorResponseDto.builder()
                            .status(HttpStatus.CONFLICT.toString())
                            .reason("Category name must be unique.")
                            .message("Category with name = " + name + " already exists.")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }
}
