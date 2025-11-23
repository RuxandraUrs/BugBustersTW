package org.example.menuservice.service;
import org.example.menuservice.dto.DishDto;
import java.util.List;

public interface IDishService {
    DishDto createDish(DishDto dishDto);
    DishDto getDishById(Integer id);
    List<DishDto> getAllDishes();
    DishDto updateDish(Integer id, DishDto dishDto);
    void deleteDish(Integer id);
    List<DishDto> searchDishByName(String name);
    List<DishDto> filterDishes(Integer categoryId, Boolean availability);
    List<DishDto> getDishesSortedBy(String sortBy, String order);
}
