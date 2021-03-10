package com.jm.online_store.controller.rest.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jm.online_store.controller.ExceptionsHandler;
import com.jm.online_store.model.SharedStock;
import com.jm.online_store.model.Stock;
import com.jm.online_store.model.dto.SharedStockDto;
import com.jm.online_store.model.dto.StockDto;
import com.jm.online_store.service.interf.SharedStockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ManagerSharedStockRestControllerTest {
    private SharedStockService sharedStockService;
    private ModelMapper modelMapper;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
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
    void getQuantity() throws Exception {
        when(sharedStockService.findAll()).thenReturn(sharedStocks);
        mockMvc.perform(get(END_POINT)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(jsonPath("$.data[*].id",  containsInAnyOrder(
                        sharedStocks.get(0).getId().intValue(),
                        sharedStocks.get(1).getId().intValue())))
                .andExpect(jsonPath("$.data[*].socialNetworkName", containsInAnyOrder(
                        sharedStocks.get(0).getSocialNetworkName(),
                        sharedStocks.get(1).getSocialNetworkName())))
                .andExpect(jsonPath("$.data[*].stockDto.id", containsInAnyOrder(
                        sharedStocks.get(0).getStock().getId().intValue(),
                        sharedStocks.get(1).getStock().getId().intValue())))
                .andExpect(jsonPath("$.data[*].stockDto.stockImg", containsInAnyOrder(
                        sharedStocks.get(0).getStock().getStockImg(),
                        sharedStocks.get(1).getStock().getStockImg())));
    }

    @Test
    @DisplayName("Get all shared stocks should returns an empty list")
    void getQuantityShouldReturnEmptyList() throws Exception {
        when(sharedStockService.findAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get(END_POINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    private SharedStockDto toSharedStockDto(SharedStock sharedStock) {
        StockDto stockDto = modelMapper.map(sharedStock.getStock(), StockDto.class);
        SharedStockDto sharedStockDto = modelMapper.map(sharedStock, SharedStockDto.class);
        sharedStockDto.setStockDto(stockDto);
        return sharedStockDto;
    }
}
