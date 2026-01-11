package org.example.menuservice.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.menuservice.dto.IngredientDto;
import org.example.menuservice.entity.Ingredient;
import org.example.menuservice.repository.DishRepository;
import org.example.menuservice.repository.IngredientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredientServiceImplTest {

    @Mock
    private IngredientRepository ingredientRepository;
    @Mock
    private DishRepository dishRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private IngredientServiceImpl ingredientService;

    @Test
    void createIngredient_Success() {
        IngredientDto inputDto = new IngredientDto();
        inputDto.setName("Pepper");

        Ingredient mappedEntity = new Ingredient();
        mappedEntity.setName("Pepper");

        Ingredient savedEntity = new Ingredient();
        savedEntity.setId(1);
        savedEntity.setName("Pepper");

        IngredientDto outputDto = new IngredientDto();
        outputDto.setId(1);
        outputDto.setName("Pepper");

        when(ingredientRepository.findByName("Pepper")).thenReturn(Optional.empty());

        when(modelMapper.map(any(IngredientDto.class), eq(Ingredient.class)))
                .thenReturn(mappedEntity);

        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(savedEntity);

        when(modelMapper.map(any(Ingredient.class), eq(IngredientDto.class)))
                .thenReturn(outputDto);

        IngredientDto result = ingredientService.createIngredient(inputDto);

        assertNotNull(result);
        assertEquals("Pepper", result.getName());
    }

    @Test
    void createIngredient_Duplicate_ThrowsException() {
        IngredientDto dto = new IngredientDto();
        dto.setName("Salt");
        Ingredient existing = new Ingredient();
        existing.setName("Salt");

        when(ingredientRepository.findByName("Salt")).thenReturn(Optional.of(existing));

        assertThrows(IllegalStateException.class, () -> ingredientService.createIngredient(dto));
    }

    @Test
    void deleteIngredient_UsedInDishes_ThrowsException() {
        int id = 1;
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Salt");

        when(ingredientRepository.findById(id)).thenReturn(Optional.of(ingredient));
        when(dishRepository.countByIngredientsContaining(ingredient)).thenReturn(5L);

        assertThrows(IllegalStateException.class, () -> ingredientService.deleteIngredient(id));
    }

    @Test
    void deleteIngredient_Success() {
        int id = 1;
        Ingredient ingredient = new Ingredient();

        when(ingredientRepository.findById(id)).thenReturn(Optional.of(ingredient));
        when(dishRepository.countByIngredientsContaining(ingredient)).thenReturn(0L);

        ingredientService.deleteIngredient(id);

        verify(ingredientRepository).deleteById(id);
    }
}