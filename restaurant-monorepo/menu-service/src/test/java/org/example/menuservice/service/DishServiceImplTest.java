package org.example.menuservice.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.menuservice.dto.DishDto;
import org.example.menuservice.entity.Category;
import org.example.menuservice.entity.Dish;
import org.example.menuservice.entity.Ingredient;
import org.example.menuservice.repository.CategoryRepository;
import org.example.menuservice.repository.DishRepository;
import org.example.menuservice.repository.IngredientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DishServiceImplTest {

    @Mock
    private DishRepository dishRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private IngredientRepository ingredientRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private DishServiceImpl dishService;

    @Test
    void createDish_Success_WithNewIngredient() {
        DishDto inputDto = new DishDto();
        inputDto.setCategoryId(1);
        inputDto.setIngredients(Set.of("Tomato", "Cheese"));

        Category mockCategory = new Category();
        mockCategory.setId(1);

        Ingredient tomato = new Ingredient();
        tomato.setName("Tomato");

        when(categoryRepository.findById(1)).thenReturn(Optional.of(mockCategory));
        when(ingredientRepository.findByName("Tomato")).thenReturn(Optional.of(tomato));
        when(ingredientRepository.findByName("Cheese")).thenReturn(Optional.empty());
        when(ingredientRepository.save(any(Ingredient.class))).thenAnswer(i -> i.getArguments()[0]);

        Dish mappedDish = new Dish();
        when(modelMapper.map(inputDto, Dish.class)).thenReturn(mappedDish);
        when(dishRepository.save(any(Dish.class))).thenReturn(mappedDish);
        when(modelMapper.map(any(Dish.class), eq(DishDto.class))).thenReturn(new DishDto());

        DishDto result = dishService.createDish(inputDto);

        assertNotNull(result);
        verify(dishRepository).save(any(Dish.class));
        verify(ingredientRepository, times(1)).save(any(Ingredient.class));
    }

    @Test
    void createDish_CategoryNotFound_ThrowsException() {
        DishDto inputDto = new DishDto();
        inputDto.setCategoryId(99);

        when(categoryRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> dishService.createDish(inputDto));
    }

    @Test
    void getDishById_Success() {
        Dish dish = new Dish();
        dish.setId(1);
        dish.setCategory(new Category());
        dish.setIngredients(new HashSet<>());

        when(dishRepository.findByIdWithCategory(1)).thenReturn(Optional.of(dish));
        when(modelMapper.map(dish, DishDto.class)).thenReturn(new DishDto());

        DishDto result = dishService.getDishById(1);
        assertNotNull(result);
    }

    @Test
    void getDishById_NotFound_ThrowsException() {
        when(dishRepository.findByIdWithCategory(1)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> dishService.getDishById(1));
    }

    @Test
    void updateDish_Success() {
        int dishId = 1;
        DishDto updateDto = new DishDto();
        updateDto.setCategoryId(2);
        updateDto.setIngredients(Set.of("Salt"));

        Dish existingDish = new Dish();
        Category newCategory = new Category();
        Ingredient salt = new Ingredient();

        when(dishRepository.findById(dishId)).thenReturn(Optional.of(existingDish));
        when(categoryRepository.findById(2)).thenReturn(Optional.of(newCategory));
        when(ingredientRepository.findByName("Salt")).thenReturn(Optional.of(salt));
        when(dishRepository.save(any(Dish.class))).thenReturn(existingDish);
        when(modelMapper.map(any(Dish.class), eq(DishDto.class))).thenReturn(new DishDto());

        DishDto result = dishService.updateDish(dishId, updateDto);

        assertNotNull(result);
        verify(dishRepository).save(existingDish);
    }

    @Test
    void deleteDish_Success() {
        when(dishRepository.existsById(1)).thenReturn(true);

        dishService.deleteDish(1);

        verify(dishRepository).deleteById(1);
    }

    @Test
    void deleteDish_NotFound_ThrowsException() {
        when(dishRepository.existsById(1)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> dishService.deleteDish(1));
    }

    @Test
    void getAllDishes_Success() {
        when(dishRepository.findAllWithCategories()).thenReturn(List.of(new Dish()));
        when(modelMapper.map(any(), eq(DishDto.class))).thenReturn(new DishDto());

        List<DishDto> result = dishService.getAllDishes();
        assertEquals(1, result.size());
    }

    @Test
    void getDishesSortedBy_Desc() {
        when(dishRepository.findAll(any(Sort.class))).thenReturn(List.of(new Dish()));
        when(modelMapper.map(any(), eq(DishDto.class))).thenReturn(new DishDto());

        List<DishDto> result = dishService.getDishesSortedBy("price", "desc");
        assertFalse(result.isEmpty());
    }
}