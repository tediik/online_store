package com.jm.online_store.controller.rest.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jm.online_store.controller.ExceptionsHandler;
import com.jm.online_store.exception.ExceptionConstants;
import com.jm.online_store.exception.StockNotFoundException;
import com.jm.online_store.model.Stock;
import com.jm.online_store.model.dto.StockDto;
import com.jm.online_store.service.interf.StockService;
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
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ManagerStockRestControllerTest {
    private MockMvc mockMvc;
    private ModelMapper modelMapper;
    private ObjectMapper objectMapper;
    private StockService stockService;
    private List<Stock> stocks;
    private final static String END_POINT = "/api/manager/stock";

    @BeforeEach
    void setUp() {
        modelMapper = new ModelMapper();
        objectMapper = new ObjectMapper();
        stockService = mock(StockService.class);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ManagerStockRestController(stockService, modelMapper))
                .setControllerAdvice(new ExceptionsHandler())
                .build();
        stocks = Arrays.asList(new Stock(1L, "aaa", "bbb", "ccc", LocalDate.now().minusMonths(7), LocalDate.now()),
                               new Stock(2L, "ddd", "eee", "fff", LocalDate.now().minusMonths(8), LocalDate.now()));
    }

    @Test
    @DisplayName("get stock by id")
    void getStockById() throws Exception {
        when(stockService.findStockById(anyLong())).thenReturn(stocks.get(0));
        mockMvc.perform(get(END_POINT + "/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(stocks.get(0).getId()))
                .andExpect(jsonPath("$.data.stockImg").value(stocks.get(0).getStockImg()))
                .andExpect(jsonPath("$.data.stockTitle").value(stocks.get(0).getStockTitle()));
    }

    @Test
    @DisplayName("get all stocks")
    void getAllStocks() throws Exception {
        when(stockService.findAll()).thenReturn(stocks);
        mockMvc.perform(get(END_POINT + "/allStocks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(stocks.size())))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        stocks.get(0).getId().intValue(),
                        stocks.get(1).getId().intValue())))
                .andExpect(jsonPath("$.data[*].stockImg", containsInAnyOrder(
                        stocks.get(0).getStockImg(),
                        stocks.get(1).getStockImg())))
                .andExpect(jsonPath("$.data[*].stockTitle", containsInAnyOrder(
                        stocks.get(0).getStockTitle(),
                        stocks.get(1).getStockTitle())));
    }

    @Test
    @DisplayName("get all current stocks")
    void getCurrentStocks() throws Exception {
        when(stockService.findCurrentStocks()).thenReturn(stocks);
        mockMvc.perform(get(END_POINT + "/currentStocks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(stocks.size())))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        stocks.get(0).getId().intValue(),
                        stocks.get(1).getId().intValue())))
                .andExpect(jsonPath("$.data[*].stockImg", containsInAnyOrder(
                        stocks.get(0).getStockImg(),
                        stocks.get(1).getStockImg())))
                .andExpect(jsonPath("$.data[*].stockTitle", containsInAnyOrder(
                        stocks.get(0).getStockTitle(),
                        stocks.get(1).getStockTitle())));
    }

    @Test
    @DisplayName("get all future stocks")
    void getFutureStocks() throws Exception {
        when(stockService.findFutureStocks()).thenReturn(stocks);
        mockMvc.perform(get(END_POINT + "/futureStocks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(stocks.size())))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        stocks.get(0).getId().intValue(),
                        stocks.get(1).getId().intValue())))
                .andExpect(jsonPath("$.data[*].stockImg", containsInAnyOrder(
                        stocks.get(0).getStockImg(),
                        stocks.get(1).getStockImg())))
                .andExpect(jsonPath("$.data[*].stockTitle", containsInAnyOrder(
                        stocks.get(0).getStockTitle(),
                        stocks.get(1).getStockTitle())));
    }

    @Test
    @DisplayName("get all past stocks")
    void getPastStocks() throws Exception {
        when(stockService.findPastStocks()).thenReturn(stocks);
        mockMvc.perform(get(END_POINT + "/pastStocks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(stocks.size())))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        stocks.get(0).getId().intValue(),
                        stocks.get(1).getId().intValue())))
                .andExpect(jsonPath("$.data[*].stockImg", containsInAnyOrder(
                        stocks.get(0).getStockImg(),
                        stocks.get(1).getStockImg())))
                .andExpect(jsonPath("$.data[*].stockTitle", containsInAnyOrder(
                        stocks.get(0).getStockTitle(),
                        stocks.get(1).getStockTitle())));
    }

    @Test
    @DisplayName("add stock")
    void addNewStock() throws Exception {
        when(stockService.addStock(any(Stock.class))).thenReturn(stocks.get(0));
        mockMvc.perform(post(END_POINT)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(toStockDto(stocks.get(0)))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(stocks.get(0).getId()))
                .andExpect(jsonPath("$.data.stockImg").value(stocks.get(0).getStockImg()))
                .andExpect(jsonPath("$.data.stockTitle").value(stocks.get(0).getStockTitle()));
    }

    @Test
    @DisplayName("delete stock by id")
    void deleteStockById() throws Exception {
        doNothing().when(stockService).deleteStockById(anyLong());
        mockMvc.perform(delete(END_POINT + "/{id}", 11)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(status().isOk());
    }

    @Test
    void modifyStock() throws Exception {
        when(stockService.updateStock(any(Stock.class))).thenReturn(stocks.get(0));
        mockMvc.perform(put(END_POINT)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(toStockDto(stocks.get(0)))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(stocks.get(0).getId()))
                .andExpect(jsonPath("$.data.stockImg").value(stocks.get(0).getStockImg()))
                .andExpect(jsonPath("$.data.stockTitle").value(stocks.get(0).getStockTitle()));
    }

    @Test
    @DisplayName("allStocks, currentStocks, pastStocks, futureStocks lists return empty data")
    void shouldReturnEmptyList() throws Exception {
        when(stockService.findAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get(END_POINT + "/allStocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
        when(stockService.findCurrentStocks()).thenReturn(Collections.emptyList());
        mockMvc.perform(get(END_POINT + "/currentStocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
        when(stockService.findPastStocks()).thenReturn(Collections.emptyList());
        mockMvc.perform(get(END_POINT + "/pastStocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
        when(stockService.findFutureStocks()).thenReturn(Collections.emptyList());
        mockMvc.perform(get(END_POINT + "/futureStocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("throws stock not found exception when try to delete stock by id")
    void deleteStockByIdThrowsStockNotFoundException() throws Exception {
        doThrow(new StockNotFoundException(ExceptionConstants.NOT_FOUND)).when(stockService).deleteStockById(anyLong());
        mockMvc.perform(delete(END_POINT + "/{id}", 11))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("throws stock not found exception")
    void getStockByIdThrowsStockNotFoundException() throws Exception {
        when(stockService.findStockById(anyLong())).thenThrow(new StockNotFoundException(ExceptionConstants.NOT_FOUND));
        mockMvc.perform(get(END_POINT + "/{id}", 11))
                .andExpect(status().isNotFound());
    }

    private StockDto toStockDto(Stock stock) {
        return modelMapper.map(stock, StockDto.class);
    }
}
