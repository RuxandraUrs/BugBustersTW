package org.example.menuservice.service;

import org.example.menuservice.dto.IngredientDto;
import org.example.menuservice.entity.Ingredient;
import org.example.menuservice.repository.DishRepository;
import org.example.menuservice.repository.IngredientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IngredientServiceImpl implements IIngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public IngredientDto createIngredient(IngredientDto ingredientDto) {
        ingredientRepository.findByName(ingredientDto.getName()).ifPresent(i -> {
            throw new IllegalStateException("Ingredient '" + i.getName() + "' already exists.");
        });

        Ingredient ingredient = modelMapper.map(ingredientDto, Ingredient.class);
        Ingredient savedIngredient = ingredientRepository.save(ingredient);
        return modelMapper.map(savedIngredient, IngredientDto.class);
    }

    @Override
    public List<IngredientDto> getAllIngredients() {
        return ingredientRepository.findAll().stream()
                .map(ingredient -> modelMapper.map(ingredient, IngredientDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public IngredientDto getIngredientById(Integer id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingredient with ID " + id + " not found."));
        return modelMapper.map(ingredient, IngredientDto.class);
    }

    @Override
    public void deleteIngredient(Integer id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingredient with ID " + id + " not found."));

        long dishCount = dishRepository.countByIngredientsContaining(ingredient);
        if (dishCount > 0) {
            throw new IllegalStateException("Cannot delete ingredient '" + ingredient.getName() +
                    "' because it is used in " + dishCount + " dishes.");
        }

        ingredientRepository.deleteById(id);
    }
}
