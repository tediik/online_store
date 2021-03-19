package com.jm.online_store.controller.rest.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jm.online_store.controller.ExceptionsHandler;
import com.jm.online_store.exception.CategoriesNotFoundException;
import com.jm.online_store.model.Categories;
import com.jm.online_store.model.dto.CategoriesDto;
import com.jm.online_store.service.interf.CategoriesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductCategoriesRestControllerTest {
    private CategoriesService categoriesService;
    private ModelMapper modelMapper;
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private List<Categories> categories;
    private List<Categories> categoriesWithoutParentId;
    private final static String END_POINT = "/api/categories";

    @BeforeEach
    void setUp() {
        modelMapper = new ModelMapper();
        objectMapper = new ObjectMapper();
        categoriesService = mock(CategoriesService.class);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ProductCategoriesRestController(categoriesService, modelMapper))
                .setControllerAdvice(new ExceptionsHandler())
                .build();
        categories = Arrays.asList(
                new Categories(1L, "phone", 1L),
                new Categories(2L, "tv", 2L)
        );
        categoriesWithoutParentId = Arrays.asList(
                new Categories(1L, "phone"),
                new Categories(2L, "tv")
        );
    }

    @Test
    @DisplayName("get all categories array node")
    void getAllCategories() throws Exception {
        when(categoriesService.findAll()).thenReturn(categories);
        mockMvc.perform(get(END_POINT + "/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("get category name by product id")
    void getCategoryNameByProductId() throws Exception {
        when(categoriesService.getCategoryNameByProductId(anyLong())).thenReturn("APPLE");
        mockMvc.perform(get(END_POINT + "/getOne/{id}", anyLong())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("APPLE"));
    }

    @Test
    @DisplayName("get category name by product id throws category name not found exception")
    void getCategoryNameByProductIdThrowsCategoryNotFoundException() throws Exception {
        when(categoriesService.getCategoryNameByProductId(anyLong())).thenThrow(CategoriesNotFoundException.class);
        mockMvc.perform(get(END_POINT + "/getOne/{id}", anyLong())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("get sub categories by product id")
    void getSubCategoriesById() throws Exception {
        when(categoriesService.getCategoriesByParentCategoryId(anyLong())).thenReturn(categories);
        mockMvc.perform(get(END_POINT + "/sub/{id}", anyLong())
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(categories.size())))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        categories.get(0).getId().intValue(),
                        categories.get(1).getId().intValue())))
                .andExpect(jsonPath("$.data[*].category", containsInAnyOrder(
                        categories.get(0).getCategory(),
                        categories.get(1).getCategory())))
                .andExpect(jsonPath("$.data[*].parentCategoryId", containsInAnyOrder(
                        categories.get(0).getParentCategoryId().intValue(),
                        categories.get(1).getParentCategoryId().intValue())));
    }

    @Test
    @DisplayName("create new category")
    void newCategory() throws Exception {
        when(categoriesService.saveCategory(categories.get(0))).thenReturn(categories.get(0));
        mockMvc.perform(post(END_POINT)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(toTopicCategoryDto(categories.get(0)))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(categories.get(0).getId()))
                .andExpect(jsonPath("$.data.category").value(categories.get(0).getCategory()))
                .andExpect(jsonPath("$.data.parentCategoryId").value(categories.get(0).getParentCategoryId()));
    }

    @Test
    @DisplayName("update category")
    void updateCategory() throws Exception {
        when(categoriesService.updateCategory(categories.get(0))).thenReturn(categories.get(1));
        mockMvc.perform(put(END_POINT).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(toTopicCategoryDto(categories.get(0)))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(categories.get(1).getId()))
                .andExpect(jsonPath("$.data.category").value(categories.get(1).getCategory()))
                .andExpect(jsonPath("$.data.parentCategoryId").value(categories.get(1).getParentCategoryId()));
    }

    @Test
    @DisplayName("delete category by id")
    void deleteCategory() throws Exception {
        when(categoriesService.deleteCategory(anyLong())).thenReturn(true);
        mockMvc.perform(delete(END_POINT + "/{id}", anyLong())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("get categories without parent category")
    void getCategoriesWithoutParentCategory() throws Exception {
        when(categoriesService.getCategoriesWithoutParentCategory()).thenReturn(categoriesWithoutParentId);
        mockMvc.perform(get(END_POINT + "/withoutParentCategory")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(categoriesWithoutParentId.size())))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        categoriesWithoutParentId.get(0).getId().intValue(),
                        categoriesWithoutParentId.get(1).getId().intValue())))
                .andExpect(jsonPath("$.data[*].category", containsInAnyOrder(
                        categoriesWithoutParentId.get(0).getCategory(),
                        categoriesWithoutParentId.get(1).getCategory())));
    }

    @Test
    @DisplayName("get all categories, get sub categories, get categories without parent category lists return empty data")
    void shouldReturnEmptyList() throws Exception {
        when(categoriesService.getCategoriesByParentCategoryId(anyLong())).thenReturn(Collections.emptyList());
        mockMvc.perform(get(END_POINT + "/sub/{id}" , anyLong()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
        when(categoriesService.getCategoriesWithoutParentCategory()).thenReturn(Collections.emptyList());
        mockMvc.perform(get(END_POINT + "/withoutParentCategory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    private CategoriesDto toTopicCategoryDto(Categories categories) {
        return modelMapper.map(categories, CategoriesDto.class);
    }
}
