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
/**
 * Implementation of the Dish management service.
 * Handles business logic for menu items, including category and ingredient associations.
 *
 * @author Ruxandra Urs
 * @version 1.0 -12.01.2026
 */
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

    /**
     * Creates a new dish and automatically handles ingredient persistence and category linking.
     *
     * @param dishDto the data transfer object containing dish details.
     * @return DishDto the persisted dish with generated ID and mapped associations.
     * @throws EntityNotFoundException if the associated category ID is invalid.
     * @author Ruxandra Urs - 12.01.2026
     */
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
    /**
     * Retrieves all dishes from the database including their category details.
     *
     * @return List&lt;DishDto&gt; a list of all dishes.
     * @author Ruxandra Urs - 12.01.2026
     */
    @Override
    @Transactional(readOnly = true)
    public List<DishDto> getAllDishes() {
        List<Dish> dishes = dishRepository.findAllWithCategories();
        return dishes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    /**
     * Retrieves a single dish by its unique identifier.
     *
     * @param id the unique ID of the dish.
     * @return DishDto the found dish data.
     * @throws EntityNotFoundException if no dish exists with the given ID.
     * @author Ruxandra Urs - 12.01.2026
     */
    @Override
    @Transactional(readOnly = true)
    public DishDto getDishById(Integer id) {
        Dish dish = dishRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new EntityNotFoundException("Dish with ID " + id + " not found."));
        return convertToDto(dish);
    }
    /**
     * Updates an existing dish with new details and validates associations.
     *
     * @param id the ID of the dish to update.
     * @param dishDto the new data to apply.
     * @return DishDto the updated dish.
     * @author Ruxandra Urs - 12.01.2026
     */
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
    /**
     * Deletes a dish from the repository based on its ID.
     *
     * @param id the ID of the dish to delete.
     * @throws EntityNotFoundException if the ID is not found.
     * @author Ruxandra Urs - 12.01.2026
     */
    @Override
    @Transactional
    public void deleteDish(Integer id) {
        if (!dishRepository.existsById(id)) {
            throw new EntityNotFoundException("Dish with ID " + id + " not found.");
        }
        dishRepository.deleteById(id);
    }

    /**
     * Searches for dishes by name using a partial match (ignore case).
     *
     * @param name the substring to search for.
     * @return List&lt;DishDto&gt; matching dishes.
     * @author Ruxandra Urs - 12.01.2026
     */
    @Override
    @Transactional(readOnly = true)
    public List<DishDto> searchDishByName(String name) {
        List<Dish> dishes = dishRepository.findByNameContainingIgnoreCaseWithCategory(name);
        return dishes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    /**
     * Filters dishes based on category identifier and availability status.
     *
     * @param categoryId the ID of the category.
     * @param availability the status of the dish.
     * @return List&lt;DishDto&gt; filtered dishes.
     * @author Ruxandra Urs - 12.01.2026
     */
    @Override
    @Transactional(readOnly = true)
    public List<DishDto> filterDishes(Integer categoryId, Boolean availability) {
        List<Dish> dishes = dishRepository.filterByCriteriaWithCategory(categoryId, availability);
        return dishes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    /**
     * Retrieves dishes sorted by a specified field and direction.
     *
     * @param sortBy field to sort by.
     * @param order "asc" or "desc".
     * @return List&lt;DishDto&gt; sorted list of dishes.
     * @author Ruxandra Urs - 12.01.2026
     */
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
    /**
     * Utility method to convert a Dish entity to a DishDto.
     *
     * @param dish the entity to convert.
     * @return DishDto the converted data object.
     * @author Ruxandra Urs - 12.01.2026
     */
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