package org.example.menuservice.service;

import org.example.menuservice.dto.CategoryDto;
import org.example.menuservice.entity.Category;
import org.example.menuservice.repository.CategoryRepository;
import org.example.menuservice.repository.DishRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
/**
 * Service implementation for managing menu categories.
 * Handles persistence and validation logic for categories.
 *
 * @author Ruxandra Urs - 12.01.2026
 * @version 1.0
 */
@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Creates a new category while ensuring the name is unique.
     *
     * @param categoryDto the category data.
     * @return CategoryDto the saved category.
     * @throws IllegalStateException if a category name already exists.
     * @author Ruxandra Urs - 12.01.2026
     */
    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        categoryRepository.findByName(categoryDto.getName()).ifPresent(c -> {
            throw new IllegalStateException("A category with the name '" + c.getName() + "' already exists.");
        });
        Category categoryToSave = new Category();
        categoryToSave.setName(categoryDto.getName());
        Category savedCategory = categoryRepository.save(categoryToSave);
        return modelMapper.map(savedCategory, CategoryDto.class);
    }

    /**
     * Retrieves all categories from the repository.
     *
     * @return List&lt;CategoryDto&gt; all categories.
     * @author Ruxandra Urs-12.01.2026
     */
    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> modelMapper.map(category, CategoryDto.class))
                .collect(Collectors.toList());
    }
    /**
     * Finds a category by its ID.
     *
     * @param id the category ID.
     * @return CategoryDto the found category.
     * @author Ruxandra Urs - 12.01.2026
     */
    @Override
    public CategoryDto getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with ID " + id + " not found."));
        return modelMapper.map(category, CategoryDto.class);
    }
    /**
     * Updates an existing category name.
     *
     * @param id the ID of the category.
     * @param categoryDto new name data.
     * @return CategoryDto updated category.
     * @author Ruxandra Urs- 12.01.2026
     */
    @Override
    public CategoryDto updateCategory(Integer id, CategoryDto categoryDto) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with ID " + id + " not found."));

        existingCategory.setName(categoryDto.getName());
        Category updatedCategory = categoryRepository.save(existingCategory);
        return modelMapper.map(updatedCategory, CategoryDto.class);
    }
    /**
     * Deletes a category if it has no associated dishes.
     *
     * @param id the category ID.
     * @throws IllegalStateException if category is still associated with dishes.
     * @author Ruxandra Urs - 12.01.2026
     */
    @Override
    public void deleteCategory(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category with ID " + id + " not found.");
        }

        long dishCount = dishRepository.findByCategoryIdWithCategory(id).size();
        if (dishCount > 0) {
            throw new IllegalStateException("Cannot delete category with ID " + id +
                    " because it is associated with " + dishCount + " dishes.");
        }

        categoryRepository.deleteById(id);
    }
}
