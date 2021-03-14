package com.jm.online_store.controller.rest.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jm.online_store.controller.ExceptionsHandler;
import com.jm.online_store.exception.ExceptionConstants;
import com.jm.online_store.exception.TopicCategoryNotFoundException;
import com.jm.online_store.model.TopicsCategory;
import com.jm.online_store.model.dto.TopicsCategoryDto;
import com.jm.online_store.service.interf.TopicsCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ManagerTopicsCategoryRestControllerTest {
    private MockMvc mockMvc;
    private ModelMapper modelMapper;
    private ObjectMapper objectMapper;
    private TopicsCategoryService topicsCategoryService;
    private List<TopicsCategory> notActualTopicsCategories;
    private List<TopicsCategory> actualTopicsCategories;
    private List<TopicsCategory> topicsCategories;
    private static final String END_POINT = "/api/manager/topicsCategory";

    @BeforeEach
    void setUp() {
        modelMapper = new ModelMapper();
        objectMapper = new ObjectMapper();
        topicsCategoryService = mock(TopicsCategoryService.class);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ManagerTopicsCategoryRestController(topicsCategoryService, modelMapper))
                .setControllerAdvice(new ExceptionsHandler())
                .build();
        notActualTopicsCategories = Arrays.asList(new TopicsCategory(1L, "free", false),
                                                  new TopicsCategory(2L, "excellent", false));

        actualTopicsCategories = Arrays.asList(new TopicsCategory(3L, "free", true),
                                               new TopicsCategory(4L, "excellent", true));
        topicsCategories = new ArrayList<>();
        topicsCategories.addAll(actualTopicsCategories);
        topicsCategories.addAll(notActualTopicsCategories);
    }

    @Test
    @DisplayName("read all topics categories")
    void readAllTopicsCategories() throws Exception {
        when(topicsCategoryService.findAll()).thenReturn(topicsCategories);
        mockMvc.perform(get(END_POINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(topicsCategories.size())))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        topicsCategories.get(0).getId().intValue(),
                        topicsCategories.get(1).getId().intValue(),
                        topicsCategories.get(2).getId().intValue(),
                        topicsCategories.get(3).getId().intValue()
                )))
                .andExpect(jsonPath("$.data[*].categoryName", containsInAnyOrder(
                        topicsCategories.get(0).getCategoryName(),
                        topicsCategories.get(1).getCategoryName(),
                        topicsCategories.get(2).getCategoryName(),
                        topicsCategories.get(3).getCategoryName()
                )))
                .andExpect(jsonPath("$.data[*].actual", containsInAnyOrder(
                        topicsCategories.get(0).getActual(),
                        topicsCategories.get(1).getActual(),
                        topicsCategories.get(2).getActual(),
                        topicsCategories.get(3).getActual()
                )));
    }

    @Test
    @DisplayName("read topic category by id")
    void readTopicsCategory() throws Exception {
        when(topicsCategoryService.findById(anyLong())).thenReturn(topicsCategories.get(0));
        mockMvc.perform(get(END_POINT + "/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(topicsCategories.get(0).getId()))
                .andExpect(jsonPath("$.data.categoryName").value(topicsCategories.get(0).getCategoryName()))
                .andExpect(jsonPath("$.data.actual").value(topicsCategories.get(0).getActual()));
    }

    @Test
    @DisplayName("create topic category")
    void createTopicsCategory() throws Exception {
        when(topicsCategoryService.create(any(TopicsCategory.class))).thenReturn(notActualTopicsCategories.get(0));
        mockMvc.perform(post(END_POINT)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(toTopicCategoryDto(notActualTopicsCategories.get(0)))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(notActualTopicsCategories.get(0).getId()))
                .andExpect(jsonPath("$.data.categoryName").value(notActualTopicsCategories.get(0).getCategoryName()))
                .andExpect(jsonPath("$.data.actual").value(notActualTopicsCategories.get(0).getActual()));
    }

    @Test
    @DisplayName("update topic category by id")
    void updateTopicsCategory() throws Exception {
        when(topicsCategoryService.updateById(anyLong(), any(TopicsCategory.class))).thenReturn(notActualTopicsCategories.get(1));
        mockMvc.perform(put(END_POINT + "/{id}" , 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(toTopicCategoryDto(notActualTopicsCategories.get(0)))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(notActualTopicsCategories.get(1).getId()))
                .andExpect(jsonPath("$.data.categoryName").value(notActualTopicsCategories.get(1).getCategoryName()))
                .andExpect(jsonPath("$.data.actual").value(notActualTopicsCategories.get(1).getActual()));
    }

    @Test
    @DisplayName("archive topic category by id")
    void archiveTopicsCategory() throws Exception {
        when(topicsCategoryService.archive(anyLong())).thenReturn(notActualTopicsCategories.get(1));
        mockMvc.perform(put(END_POINT + "/archive/{id}" , 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(toTopicCategoryDto(notActualTopicsCategories.get(0)))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(notActualTopicsCategories.get(1).getId()))
                .andExpect(jsonPath("$.data.categoryName").value(notActualTopicsCategories.get(1).getCategoryName()))
                .andExpect(jsonPath("$.data.actual").value(notActualTopicsCategories.get(1).getActual()));
    }

    @Test
    @DisplayName("unarchive topic category by id")
    void unarchiveTopicsCategory() throws Exception {
        when(topicsCategoryService.unarchive(anyLong())).thenReturn(actualTopicsCategories.get(1));
        mockMvc.perform(put(END_POINT + "/unarchive/{id}" , 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(toTopicCategoryDto(actualTopicsCategories.get(0)))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(actualTopicsCategories.get(1).getId()))
                .andExpect(jsonPath("$.data.categoryName").value(actualTopicsCategories.get(1).getCategoryName()))
                .andExpect(jsonPath("$.data.actual").value(actualTopicsCategories.get(1).getActual()));
    }

    @Test
    @DisplayName("read all topic categories list return empty data")
    void readAllTopicsCategoriesShouldReturnEmptyList() throws Exception {
        when(topicsCategoryService.findAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get(END_POINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("read topic category by id throws Topic category not found exception")
    void readTopicsCategoryByIdThrowsTopicCategoryNotFoundException() throws Exception {
        when(topicsCategoryService.findById(anyLong())).thenThrow(new TopicCategoryNotFoundException(ExceptionConstants.NOT_FOUND));
        mockMvc.perform(get(END_POINT + "/{id}", 11))
                .andExpect(status().isNotFound());
    }

    private TopicsCategoryDto toTopicCategoryDto(TopicsCategory topicsCategory) {
        return modelMapper.map(topicsCategory, TopicsCategoryDto.class);
    }
}
