package org.example.menuservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.menuservice.dto.DishDto;
import org.example.menuservice.service.DishServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DishController.class)
@AutoConfigureMockMvc(addFilters = false)
class DishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DishServiceImpl dishService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createDish_ReturnsCreated() throws Exception {
        DishDto dishDto = new DishDto();
        dishDto.setName("Pizza");

        when(dishService.createDish(any(DishDto.class))).thenReturn(dishDto);

        mockMvc.perform(post("/api/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dishDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Pizza"));
    }

    @Test
    void getAllDishes_ReturnsList() throws Exception {
        DishDto d1 = new DishDto();
        d1.setName("Pizza");
        List<DishDto> list = Arrays.asList(d1);

        when(dishService.getAllDishes()).thenReturn(list);

        mockMvc.perform(get("/api/dishes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void getDishById_ReturnsDish() throws Exception {
        DishDto dish = new DishDto();
        dish.setName("Burger");

        when(dishService.getDishById(1)).thenReturn(dish);

        mockMvc.perform(get("/api/dishes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Burger"));
    }

    @Test
    void updateDish_ReturnsUpdatedDish() throws Exception {
        DishDto dishDto = new DishDto();
        dishDto.setName("Updated Pizza");

        when(dishService.updateDish(eq(1), any(DishDto.class))).thenReturn(dishDto);

        mockMvc.perform(put("/api/dishes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dishDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Pizza"));
    }

    @Test
    void deleteDish_ReturnsNoContent() throws Exception {
        doNothing().when(dishService).deleteDish(1);

        mockMvc.perform(delete("/api/dishes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void searchDishes_ReturnsList() throws Exception {
        when(dishService.searchDishByName("Pizza")).thenReturn(List.of(new DishDto()));

        mockMvc.perform(get("/api/dishes/search").param("name", "Pizza"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void filterDishes_ReturnsList() throws Exception {
        when(dishService.filterDishes(1, true)).thenReturn(List.of(new DishDto()));

        mockMvc.perform(get("/api/dishes/filter")
                        .param("categoryId", "1")
                        .param("availability", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void getSortedDishes_ReturnsList() throws Exception {
        when(dishService.getDishesSortedBy("price", "asc")).thenReturn(List.of(new DishDto()));

        mockMvc.perform(get("/api/dishes/sorted")
                        .param("sortBy", "price")
                        .param("order", "asc"))
                .andExpect(status().isOk());
    }
}