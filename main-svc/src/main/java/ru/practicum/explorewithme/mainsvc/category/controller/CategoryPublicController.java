package ru.practicum.explorewithme.mainsvc.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.mainsvc.category.dto.CategoryDto;
import ru.practicum.explorewithme.mainsvc.category.service.CategoryService;
import ru.practicum.explorewithme.mainsvc.common.requests.PaginationRequest;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable @Positive Long catId) {
        log.info("Get category with id {} (/categories/{} GET).", catId, catId);
        return categoryService.getCategoryById(catId);
    }

    @GetMapping
    public List<CategoryDto> getCategories(@ModelAttribute @Validated PaginationRequest request) {
        log.info("Get categories (/categories?from={}&size={} GET).", request.getFrom(), request.getSize());
        return categoryService.getCategories(request);
    }
}
