package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.CategoryDto;
import ru.practicum.category.CategoryService;
import ru.practicum.category.NewCategoryDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class AdminCategoriesController {

    private final CategoryService categoryService;

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    CategoryDto addCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {

        CategoryDto category = categoryService.createCategory(newCategoryDto);
        return category;

    }

    @DeleteMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCategory(@PathVariable("catId") Long catId) {

        if (!categoryService.getCategory(catId).getCategoryEvents().isEmpty()) {
            throw new DataIntegrityViolationException("<admin> The category is not empty");
        } else {
            categoryService.deleteCategory(catId);
        }

    }

    @PatchMapping("/admin/categories/{catId}")
    CategoryDto updateCategory(@PathVariable("catId") Long catId, @RequestBody CategoryDto categoryDto) {

        return categoryService.updateCategory(catId, categoryDto);
    }

}
