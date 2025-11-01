package org.example.menuservice.controller;

import org.example.menuservice.dto.IngredientDto;
import org.example.menuservice.service.IngredientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    @Autowired
    private IngredientServiceImpl ingredientService;

    @PostMapping
    public ResponseEntity<IngredientDto> createIngredient(@RequestBody IngredientDto ingredientDto) {
        IngredientDto newIngredient = ingredientService.createIngredient(ingredientDto);
        return new ResponseEntity<>(newIngredient, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<IngredientDto>> getAllIngredients() {
        List<IngredientDto> ingredients = ingredientService.getAllIngredients();
        return new ResponseEntity<>(ingredients, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientDto> getIngredientById(@PathVariable Integer id) {
        IngredientDto ingredient = ingredientService.getIngredientById(id);
        return new ResponseEntity<>(ingredient, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Integer id) {
        ingredientService.deleteIngredient(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

