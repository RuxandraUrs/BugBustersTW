package org.example.menuservice.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.menuservice.dto.CategoryDto;
import org.example.menuservice.entity.Category;
import org.example.menuservice.entity.Dish;
import org.example.menuservice.repository.CategoryRepository;
import org.example.menuservice.repository.DishRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private DishRepository dishRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void createCategory_Success() {
        CategoryDto dto = new CategoryDto();
        dto.setName("Drinks");

        when(categoryRepository.findByName("Drinks")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(new Category());
        when(modelMapper.map(any(), eq(CategoryDto.class))).thenReturn(dto);

        CategoryDto result = categoryService.createCategory(dto);
        assertNotNull(result);
    }

    @Test
    void createCategory_AlreadyExists_ThrowsException() {
        CategoryDto dto = new CategoryDto();
        dto.setName("Drinks");
        Category existing = new Category();
        existing.setName("Drinks");

        when(categoryRepository.findByName("Drinks")).thenReturn(Optional.of(existing));

        assertThrows(IllegalStateException.class, () -> categoryService.createCategory(dto));
    }

    @Test
    void deleteCategory_Success() {
        int id = 1;
        when(categoryRepository.existsById(id)).thenReturn(true);
        when(dishRepository.findByCategoryIdWithCategory(id)).thenReturn(Collections.emptyList());

        categoryService.deleteCategory(id);

        verify(categoryRepository).deleteById(id);
    }

    @Test
    void deleteCategory_WithAssociatedDishes_ThrowsException() {
        int id = 1;
        when(categoryRepository.existsById(id)).thenReturn(true);
        when(dishRepository.findByCategoryIdWithCategory(id)).thenReturn(List.of(new Dish()));

        assertThrows(IllegalStateException.class, () -> categoryService.deleteCategory(id));
        verify(categoryRepository, never()).deleteById(id);
    }

    @Test
    void updateCategory_NotFound() {
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> categoryService.updateCategory(1, new CategoryDto()));
    }
}