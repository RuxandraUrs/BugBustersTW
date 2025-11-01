package org.example.menuservice.service;

import org.example.menuservice.dto.DishDto;
import org.example.menuservice.entity.Category;
import org.example.menuservice.entity.Dish;
import org.example.menuservice.entity.Ingredient;
import org.example.menuservice.repository.CategoryRepository;
import org.example.menuservice.repository.DishRepository;
import org.example.menuservice.repository.IngredientRepository;
import jakarta.persistence.EntityNotFoundException;
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
                .map(ingredientName -> ingredientRepository.findByName(ingredientName)
                        .orElseGet(() -> {
                            Ingredient newIngredient = new Ingredient();
                            newIngredient.setName(ingredientName);
                            return ingredientRepository.save(newIngredient);
                        }))
                .collect(Collectors.toSet());

        Dish dish = convertToEntity(dishDto);
        dish.setCategory(category);
        dish.setIngredients(ingredients);

        Dish savedDish = dishRepository.save(dish);
        return convertToDto(savedDish);
    }

    @Override
    public DishDto getDishById(Integer id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dish with ID " + id + " not found."));
        return convertToDto(dish);
    }

    @Override
    public List<DishDto> getAllDishes() {
        return dishRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DishDto updateDish(Integer id, DishDto dishDto) {
        Dish existingDish = dishRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dish with ID " + id + " not found."));

        Category category = categoryRepository.findById(dishDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category with ID " + dishDto.getCategoryId() + " not found."));

        Set<Ingredient> ingredients = dishDto.getIngredients().stream()
                .map(ingredientName -> ingredientRepository.findByName(ingredientName)
                        .orElseGet(() -> {
                            Ingredient newIngredient = new Ingredient();
                            newIngredient.setName(ingredientName);
                            return ingredientRepository.save(newIngredient);
                        }))
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
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dish with ID " + id + " not found."));

        dish.getIngredients().clear();
        dishRepository.save(dish);
        dishRepository.deleteById(id);
    }

    @Override
    public List<DishDto> searchDishByName(String name) {
        return dishRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DishDto> filterDishes(Integer categoryId, Boolean availability) {
        return dishRepository.filterByCriteria(categoryId, availability).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DishDto> getDishesSortedBy(String sortBy, String order) {
        if (!List.of("name", "price", "availability").contains(sortBy)) {
            sortBy = "name";
        }

        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);

        return dishRepository.findAll(sort).stream()
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
            dishDto.setIngredients(dish.getIngredients().stream()
                    .map(Ingredient::getName)
                    .collect(Collectors.toSet()));
        }
        if (dish.getPrice() != null) {
            dishDto.setPrice(dish.getPrice());
        }

        return dishDto;
    }

    private Dish convertToEntity(DishDto dishDto) {
        Dish dish = modelMapper.map(dishDto, Dish.class);
        dish.setPrice(dishDto.getPrice());

        if (dishDto.getId() == null) {
            dish.setId(null);
        }
        return dish;
    }
}
