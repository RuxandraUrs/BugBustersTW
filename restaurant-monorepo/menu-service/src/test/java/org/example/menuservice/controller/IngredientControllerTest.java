package org.example.menuservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.menuservice.dto.IngredientDto;
import org.example.menuservice.service.IngredientServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IngredientController.class)
@AutoConfigureMockMvc(addFilters = false)
class IngredientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IngredientServiceImpl ingredientService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createIngredient_ReturnsCreated() throws Exception {
        IngredientDto dto = new IngredientDto();
        dto.setName("Salt");

        when(ingredientService.createIngredient(any(IngredientDto.class))).thenReturn(dto);

        mockMvc.perform(post("/api/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Salt"));
    }

    @Test
    void getAllIngredients_ReturnsList() throws Exception {
        when(ingredientService.getAllIngredients()).thenReturn(Collections.singletonList(new IngredientDto()));

        mockMvc.perform(get("/api/ingredients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void getIngredientById_ReturnsIngredient() throws Exception {
        IngredientDto dto = new IngredientDto();
        dto.setName("Salt");

        when(ingredientService.getIngredientById(1)).thenReturn(dto);

        mockMvc.perform(get("/api/ingredients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Salt"));
    }

    @Test
    void deleteIngredient_ReturnsNoContent() throws Exception {
        doNothing().when(ingredientService).deleteIngredient(1);

        mockMvc.perform(delete("/api/ingredients/1"))
                .andExpect(status().isNoContent());
    }
}