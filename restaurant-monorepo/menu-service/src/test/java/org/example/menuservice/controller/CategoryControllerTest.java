package org.example.menuservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.menuservice.dto.CategoryDto;
import org.example.menuservice.service.CategoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryServiceImpl categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCategory_ReturnsCreated() throws Exception {
        CategoryDto dto = new CategoryDto();
        dto.setName("Drinks");

        when(categoryService.createCategory(any(CategoryDto.class))).thenReturn(dto);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Drinks"));
    }

    @Test
    void getAllCategories_ReturnsList() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(new CategoryDto()));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void getCategoryById_ReturnsCategory() throws Exception {
        CategoryDto dto = new CategoryDto();
        dto.setName("Drinks");
        when(categoryService.getCategoryById(1)).thenReturn(dto);

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Drinks"));
    }

    @Test
    void updateCategory_ReturnsUpdated() throws Exception {
        CategoryDto dto = new CategoryDto();
        dto.setName("New Drinks");

        when(categoryService.updateCategory(eq(1), any(CategoryDto.class))).thenReturn(dto);

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCategory_ReturnsNoContent() throws Exception {
        doNothing().when(categoryService).deleteCategory(1);

        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent());
    }
}