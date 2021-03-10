package com.jm.online_store.controller.rest.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jm.online_store.controller.ExceptionsHandler;
import com.jm.online_store.model.News;
import com.jm.online_store.model.SharedStock;
import com.jm.online_store.model.Stock;
import com.jm.online_store.model.dto.NewsDto;
import com.jm.online_store.model.dto.SharedStockDto;
import com.jm.online_store.model.dto.StockDto;
import com.jm.online_store.service.interf.SharedStockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ManagerSharedStockRestControllerTest {

    private SharedStockService sharedStockService;
    private ModelMapper modelMapper;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Type listType1;
    private Type listType2;
    private List<SharedStock> sharedStocks;
    private final static String END_POINT = "/api/manager/sharedStock";

    @BeforeEach
    void setUp() {
        sharedStockService = mock(SharedStockService.class);
        modelMapper = new ModelMapper();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mockMvc = MockMvcBuilders
                    .standaloneSetup(new ManagerSharedStockRestController(sharedStockService, modelMapper))
                    .setControllerAdvice(new ExceptionsHandler())
                    .build();
        listType1 = new TypeToken<List<SharedStockDto>>() {}.getType();
        listType2 = new TypeToken<List<StockDto>>() {}.getType();
        sharedStocks = Arrays.asList(
                new SharedStock(1L ,"facebook", new Stock(1L, "aaa", "bbb", "ccc", LocalDate.now().minusMonths(7), LocalDate.now())),
                new SharedStock(1L ,"vk", new Stock(2L, "ddd", "eee", "fff", LocalDate.now().minusMonths(8), LocalDate.now())));
    }

    @Test
    @DisplayName("Add shared stock")
    void addSharedStock() throws Exception {
        when(sharedStockService.addSharedStock(any(SharedStock.class))).thenReturn(sharedStocks.get(0));
        mockMvc.perform(post(END_POINT)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(toSharedStockDto(sharedStocks.get(0)))))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Get all shared stocks")
    void getQuantity() {

    }

    private SharedStockDto toSharedStockDto(SharedStock sharedStock) {
        StockDto stockDto = modelMapper.map(sharedStock.getStock(), StockDto.class);
        SharedStockDto sharedStockDto = modelMapper.map(sharedStock, SharedStockDto.class);
        sharedStockDto.setStockDto(stockDto);
        return sharedStockDto;
    }
}
