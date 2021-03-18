package com.jm.online_store.controller.rest.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jm.online_store.controller.ExceptionsHandler;
import com.jm.online_store.exception.ExceptionConstants;
import com.jm.online_store.exception.TopicNotFoundException;
import com.jm.online_store.model.Topic;
import com.jm.online_store.model.TopicsCategory;
import com.jm.online_store.model.dto.TopicDto;
import com.jm.online_store.service.interf.TopicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ManagerTopicRestControllerTest {
    private MockMvc mockMvc;
    private TopicService topicService;
    private ModelMapper modelMapper;
    private ObjectMapper objectMapper;
    private final static String END_POINT = "/api/manager/topic";
    private Topic topic;

    @BeforeEach
    void setUp() {
        topicService = mock(TopicService.class);
        modelMapper = new ModelMapper();
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ManagerTopicRestController(topicService))
                .setControllerAdvice(new ExceptionsHandler())
                .build();
        topic = new Topic(1L, "free", new TopicsCategory(1L, "marker", true));
    }

    @Test
    @DisplayName("read topic by id")
    void readTopicById() throws Exception {
        when(topicService.findById(anyLong())).thenReturn(topic);
        mockMvc.perform(get(END_POINT + "/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(topic.getId()))
                .andExpect(jsonPath("$.data.topicName").value(topic.getTopicName()))
                .andExpect(jsonPath("$.data.topicsCategory.id").value(topic.getTopicsCategory().getId()))
                .andExpect(jsonPath("$.data.topicsCategory.categoryName").value(topic.getTopicsCategory().getCategoryName()));
    }

    @Test
    @DisplayName("create topic")
    void createTopic() throws Exception {
        when(topicService.create(any(Topic.class))).thenReturn(topic);
        mockMvc.perform(post(END_POINT)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(toTopicDto(topic))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(topic.getId()))
                .andExpect(jsonPath("$.data.topicName").value(topic.getTopicName()))
                .andExpect(jsonPath("$.data.topicsCategory.id").value(topic.getTopicsCategory().getId()))
                .andExpect(jsonPath("$.data.topicsCategory.categoryName").value(topic.getTopicsCategory().getCategoryName()));
    }

    @Test
    @DisplayName("edit topic")
    void editTopic() throws Exception {
        when(topicService.update(any(Topic.class))).thenReturn(topic);
        mockMvc.perform(put(END_POINT)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(toTopicDto(topic))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(topic.getId()))
                .andExpect(jsonPath("$.data.topicName").value(topic.getTopicName()))
                .andExpect(jsonPath("$.data.topicsCategory.id").value(topic.getTopicsCategory().getId()))
                .andExpect(jsonPath("$.data.topicsCategory.categoryName").value(topic.getTopicsCategory().getCategoryName()));
    }

    @Test
    @DisplayName("throws topic not found exception")
    void getTopicByIdThrowsTopicNotFoundException() throws Exception {
        when(topicService.findById(anyLong())).thenThrow(new TopicNotFoundException(ExceptionConstants.NOT_FOUND));
        mockMvc.perform(get(END_POINT + "/{id}", 11))
                .andExpect(status().isNotFound());
    }

    private TopicDto toTopicDto(Topic topic) {
        return modelMapper.map(topic, TopicDto.class);
    }
}
