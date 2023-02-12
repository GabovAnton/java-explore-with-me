package ru.practicum.category;

import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Named;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.exception.EntityNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    private final CategoryRepository categoryRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public CategoryDto createCategory(@Valid NewCategoryDto newCategoryDto) {

        Category entity = categoryMapper.newToEntity(newCategoryDto);
        Category category = categoryRepository.save(entity);
        log.debug("Category with id: {}  successfully created", category.getId());

        return CategoryMapper.INSTANCE.toDto(category);
    }

    @Override
    public void deleteCategory(long id) {

        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "<admin>error while trying to delete Category with id: " + id + " " + "entity not found");
        }
        categoryRepository.deleteById(id);
        log.debug("Category with id: {} successfully deleted", id);

    }

    @Override
    public CategoryDto updateCategory(Long id, @Valid CategoryDto categoryDto) {

        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "error while trying to update Category with id: " + id + "Reason: category " + "doesn't exist");
        }
        Category category = categoryRepository.getReferenceById(id);
        Category updatedCategory = categoryMapper.partialUpdate(categoryDto, category);
        CategoryDto updatedCategoryDto = categoryMapper.toDto(categoryRepository.save(updatedCategory));
        log.debug("item with id: {}  updated: {}", id, updatedCategory);
        return updatedCategoryDto;

    }

    @Override
    public List<CategoryDto> getCategories(@Valid Integer from, @Valid Integer size) {

        QCategory qCategory = QCategory.category;

        JPAQuery<Category> query = new JPAQuery<>(entityManager);
        int offset = from != null ? (from > 1 ? --from : from) : 0;
        long totalItems = categoryRepository.count() + 1;

        List<CategoryDto> categoryDtos = new ArrayList<>();
        for (Category category : query.from(qCategory).limit(size != null ? size : totalItems).offset(offset).fetch()) {
            if (category != null) {
                CategoryDto categoryDto = categoryMapper.toDto(category);
                categoryDtos.add(categoryDto);
            }
        }
        return Collections.unmodifiableList(categoryDtos);
    }

    @Override
    public CategoryDto getCategoryDto(Long id) {

        return categoryRepository.getCategoryDtoById(id).orElseThrow(() -> new EntityNotFoundException(
                "category with " + "id: " + id + " not found"));
    }

    @Override
    @Named("getCategory")
    public Category getCategory(Long id) {

        return categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "category with id: " + id + " not found"));
    }

}
