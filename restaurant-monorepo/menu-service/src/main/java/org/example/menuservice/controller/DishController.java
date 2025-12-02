package org.example.menuservice.controller;

import org.example.menuservice.dto.DishDto;
import org.example.menuservice.service.DishServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dishes")
public class DishController {

    @Autowired
    private DishServiceImpl dishService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<DishDto> createDish(@RequestBody DishDto dishDto) {
        DishDto newDish = dishService.createDish(dishDto);
        return new ResponseEntity<>(newDish, HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<DishDto>> getAllDishes() {
        List<DishDto> dishes = dishService.getAllDishes();
        return new ResponseEntity<>(dishes, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<DishDto> getDishById(@PathVariable Integer id) {
        DishDto dish = dishService.getDishById(id);
        return new ResponseEntity<>(dish, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<DishDto> updateDish(@PathVariable Integer id, @RequestBody DishDto dishDto) {
        DishDto updatedDish = dishService.updateDish(id, dishDto);
        return new ResponseEntity<>(updatedDish, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDish(@PathVariable Integer id) {
        dishService.deleteDish(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/search")
    public ResponseEntity<List<DishDto>> searchDishes(@RequestParam String name) {
        List<DishDto> dishes = dishService.searchDishByName(name);
        return new ResponseEntity<>(dishes, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/filter")
    public ResponseEntity<List<DishDto>> filterDishes(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Boolean availability) {
        List<DishDto> dishes = dishService.filterDishes(categoryId, availability);
        return new ResponseEntity<>(dishes, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/sorted")
    public ResponseEntity<List<DishDto>> getSortedDishes(
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String order) {
        List<DishDto> dishes = dishService.getDishesSortedBy(sortBy, order);
        return new ResponseEntity<>(dishes, HttpStatus.OK);
    }
}

