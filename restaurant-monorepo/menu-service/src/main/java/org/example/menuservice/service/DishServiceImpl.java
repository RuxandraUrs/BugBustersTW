package org.example.menuservice.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.menuservice.dto.DishDto;
import org.example.menuservice.entity.Category;
import org.example.menuservice.entity.Dish;
import org.example.menuservice.entity.Ingredient;
import org.example.menuservice.repository.CategoryRepository;
import org.example.menuservice.repository.DishRepository;
import org.example.menuservice.repository.IngredientRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl implements IDishService {

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public DishDto createDish(DishDto dishDto) {
        Category category = categoryRepository.findById(dishDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category with ID " + dishDto.getCategoryId() + " not found."));

        Set<Ingredient> ingredients = dishDto.getIngredients().stream()
                .map(name -> ingredientRepository.findByName(name)
                        .orElseGet(() -> {
                            Ingredient newIngredient = new Ingredient();
                            newIngredient.setName(name);
                            return ingredientRepository.save(newIngredient);
                        }))
                .collect(Collectors.toSet());

        Dish dish = modelMapper.map(dishDto, Dish.class);

        dish.setCategory(category);
        dish.setIngredients(ingredients);
        dish.setId(null);
        Dish savedDish = dishRepository.save(dish);

        return convertToDto(savedDish);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DishDto> getAllDishes() {
        List<Dish> dishes = dishRepository.findAllWithCategories();
        return dishes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DishDto getDishById(Integer id) {
        Dish dish = dishRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new EntityNotFoundException("Dish with ID " + id + " not found."));
        return convertToDto(dish);
    }

    @Override
    @Transactional
    public DishDto updateDish(Integer id, DishDto dishDto) {
        Dish existingDish = dishRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dish with ID " + id + " not found."));

        Category category = categoryRepository.findById(dishDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category with ID " + dishDto.getCategoryId() + " not found."));

        Set<Ingredient> ingredients = dishDto.getIngredients().stream()
                .map(name -> ingredientRepository.findByName(name)
                        .orElseThrow(() -> new EntityNotFoundException("Ingredient '" + name + "' not found.")))
                .collect(Collectors.toSet());

        existingDish.setName(dishDto.getName());
        existingDish.setDescription(dishDto.getDescription());
        existingDish.setPrice(dishDto.getPrice());
        existingDish.setAvailability(dishDto.isAvailability());
        existingDish.setCategory(category);
        existingDish.setIngredients(ingredients);

        Dish updatedDish = dishRepository.save(existingDish);
        return convertToDto(updatedDish);
    }

    @Override
    @Transactional
    public void deleteDish(Integer id) {
        if (!dishRepository.existsById(id)) {
            throw new EntityNotFoundException("Dish with ID " + id + " not found.");
        }
        dishRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DishDto> searchDishByName(String name) {
        List<Dish> dishes = dishRepository.findByNameContainingIgnoreCaseWithCategory(name);
        return dishes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DishDto> filterDishes(Integer categoryId, Boolean availability) {
        List<Dish> dishes = dishRepository.filterByCriteriaWithCategory(categoryId, availability);
        return dishes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DishDto> getDishesSortedBy(String sortBy, String order) {
        Sort.Direction direction = order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);

        List<Dish> dishes = dishRepository.findAll(sort);

        return dishes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private DishDto convertToDto(Dish dish) {
        DishDto dishDto = modelMapper.map(dish, DishDto.class);

        if (dish.getCategory() != null) {
            dishDto.setCategoryId(dish.getCategory().getId());
            dishDto.setCategoryName(dish.getCategory().getName());
        }

        if (dish.getIngredients() != null) {
            Set<String> ingredientNames = dish.getIngredients().stream()
                    .map(Ingredient::getName)
                    .collect(Collectors.toSet());
            dishDto.setIngredients(ingredientNames);
        }

        return dishDto;
    }
}