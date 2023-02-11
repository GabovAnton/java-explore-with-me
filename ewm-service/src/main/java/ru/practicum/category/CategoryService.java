package ru.practicum.category;

import javax.validation.Valid;
import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(@Valid NewCategoryDto newCategoryDto);

    void deleteCategory(long id);

    CategoryDto updateCategory(Long id, @Valid CategoryDto categoryDto);

    List<CategoryDto> getCategories(@Valid Integer from, @Valid Integer size);

    CategoryDto getCategoryDto(Long id);

    Category getCategory(Long id);

}
