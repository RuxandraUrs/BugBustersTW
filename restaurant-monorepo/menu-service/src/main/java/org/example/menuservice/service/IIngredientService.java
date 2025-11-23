package org.example.menuservice.service;

import org.example.menuservice.dto.IngredientDto;
import java.util.List;

public interface IIngredientService {
    IngredientDto createIngredient(IngredientDto ingredientDto);
    List<IngredientDto> getAllIngredients();
    IngredientDto getIngredientById(Integer id);
    void deleteIngredient(Integer id);
}

