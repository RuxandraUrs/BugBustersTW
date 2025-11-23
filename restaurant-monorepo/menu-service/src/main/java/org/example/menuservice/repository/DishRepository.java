package org.example.menuservice.repository;

import org.example.menuservice.entity.Dish;
import org.example.menuservice.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DishRepository extends JpaRepository<Dish, Integer> {

    @Query("SELECT d FROM Dish d JOIN FETCH d.category WHERE d.id = :id")
    Optional<Dish> findByIdWithCategory(@Param("id") Integer id);

    @Query("SELECT d FROM Dish d JOIN FETCH d.category")
    List<Dish> findAllWithCategories();

    @Query("SELECT d FROM Dish d JOIN FETCH d.category WHERE lower(d.name) LIKE lower(concat('%', :name, '%'))")
    List<Dish> findByNameContainingIgnoreCaseWithCategory(@Param("name") String name);

    @Query("SELECT d FROM Dish d JOIN FETCH d.category " +
            "WHERE (:categoryId IS NULL OR d.category.id = :categoryId) " +
            "AND (:availability IS NULL OR d.availability = :availability)")
    List<Dish> filterByCriteriaWithCategory(@Param("categoryId") Integer categoryId,
                                            @Param("availability") Boolean availability);

    @Query("SELECT d FROM Dish d JOIN FETCH d.category WHERE d.category.id = :categoryId")
    List<Dish> findByCategoryIdWithCategory(@Param("categoryId") Integer categoryId);

    long countByIngredientsContaining(Ingredient ingredient);
}

