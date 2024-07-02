package ru.practicum.explorewithme.mainsvc.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.mainsvc.category.dto.CategoryDto;
import ru.practicum.explorewithme.mainsvc.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@Validated
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto postCategory(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("/admin/categories POST. Body : {}", categoryDto);
        return categoryService.addCategory(categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable @Positive Long catId) {
        log.info("/admin/categories/{} DELETE.", catId);
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto patchCategory(@PathVariable @Positive Long catId, @RequestBody @Valid CategoryDto categoryDto) {
        log.info("/admin/categories/{} PATCH. Body : {}", catId, categoryDto);
        return categoryService.patchCategory(catId, categoryDto);
    }
}
