package org.example.menuservice.repository;

import org.example.menuservice.entity.Dish;
import org.example.menuservice.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Integer> {


    List<Dish> findByNameContainingIgnoreCase(String name);

    @Query("SELECT d FROM Dish d " +
            "WHERE (:categoryId IS NULL OR d.category.id = :categoryId) " +
            "AND (:availability IS NULL OR d.availability = :availability)")
    List<Dish> filterByCriteria(@Param("categoryId") Integer categoryId,
                                @Param("availability") Boolean availability);

    List<Dish> findByCategoryId(Integer categoryId);

    long countByIngredientsContaining(Ingredient ingredient);
}

