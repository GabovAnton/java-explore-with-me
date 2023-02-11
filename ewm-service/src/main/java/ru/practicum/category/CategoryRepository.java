package ru.practicum.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    @Query("select new ru.practicum.category.CategoryDto(c.id, c.name)  from Category c where c.id = ?1")
    Optional<CategoryDto> getCategoryDtoById(Long id);

}