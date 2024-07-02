package ru.practicum.explorewithme.mainsvc.common.util.repositories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.mainsvc.category.entity.Category;
import ru.practicum.explorewithme.mainsvc.category.repository.CategoryRepository;
import ru.practicum.explorewithme.mainsvc.exception.EntityNotFoundException;
import ru.practicum.explorewithme.mainsvc.exception.dto.ErrorResponseDto;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryRepositoryHelper implements RepositoryHelper<Category> {
    private final CategoryRepository categoryRepository;

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                ErrorResponseDto.builder()
                        .status("NOT_FOUND")
                        .reason("Category not found.")
                        .message("Category with id = " + id + " not found.")
                        .timestamp(LocalDateTime.now())
                        .build()
        ));
    }

    @Override
    public void checkExistence(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    ErrorResponseDto.builder()
                            .status("NOT_FOUND")
                            .reason("Category not found.")
                            .message("Category with id = " + id + " not found.")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }
}
