package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.CategoryDto;
import ru.practicum.category.CategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class PublicCategoriesController {

    private final CategoryService categoryService;

    @GetMapping()
    List<CategoryDto> getCategories(@RequestParam(value = "from", required = false, defaultValue = "0") Integer from,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {

        return categoryService.getCategories(from, size);
    }

    @GetMapping("{catId}")
    CategoryDto getCategory(@PathVariable("catId") Long catId) {

        return categoryService.getCategoryDto(catId);
    }

}
