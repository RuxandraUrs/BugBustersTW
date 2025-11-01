package org.example.menuservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "Ingredient")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @ManyToMany(mappedBy = "ingredients", fetch = FetchType.LAZY)
    private Set<Dish> dishes;
}
