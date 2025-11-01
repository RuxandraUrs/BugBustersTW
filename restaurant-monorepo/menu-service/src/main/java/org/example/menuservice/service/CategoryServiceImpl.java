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

@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        categoryRepository.findByName(categoryDto.getName()).ifPresent(c -> {
            throw new IllegalStateException("A category with the name '" + c.getName() + "' already exists.");
        });

        Category category = modelMapper.map(categoryDto, Category.class);
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDto.class);
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> modelMapper.map(category, CategoryDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with ID " + id + " not found."));
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public CategoryDto updateCategory(Integer id, CategoryDto categoryDto) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with ID " + id + " not found."));

        existingCategory.setName(categoryDto.getName());
        Category updatedCategory = categoryRepository.save(existingCategory);
        return modelMapper.map(updatedCategory, CategoryDto.class);
    }

    @Override
    public void deleteCategory(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category with ID " + id + " not found.");
        }

        long dishCount = dishRepository.findByCategoryId(id).size();
        if (dishCount > 0) {
            throw new IllegalStateException("Cannot delete category with ID " + id +
                    " because it is associated with " + dishCount + " dishes.");
        }

        categoryRepository.deleteById(id);
    }
}
