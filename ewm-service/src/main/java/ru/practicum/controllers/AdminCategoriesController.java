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
@RequestMapping("/admin/categories")
public class AdminCategoriesController {

    private final CategoryService categoryService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    CategoryDto addCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {

        CategoryDto category = categoryService.createCategory(newCategoryDto);
        return category;

    }

    @DeleteMapping("{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCategory(@PathVariable("catId") Long catId) {

        if (!categoryService.getCategory(catId).getCategoryEvents().isEmpty()) {
            throw new DataIntegrityViolationException("<admin> The category is not empty");
        } else {
            categoryService.deleteCategory(catId);
        }

    }

    @PatchMapping("{catId}")
    CategoryDto updateCategory(@PathVariable("catId") Long catId, @RequestBody CategoryDto categoryDto) {

        return categoryService.updateCategory(catId, categoryDto);
    }

}
