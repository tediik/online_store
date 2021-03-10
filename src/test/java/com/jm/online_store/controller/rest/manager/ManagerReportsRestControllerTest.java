package com.jm.online_store.controller.rest.manager;

import com.jm.online_store.controller.ExceptionsHandler;
import com.jm.online_store.enums.DayOfWeekForStockSend;
import com.jm.online_store.exception.CustomerNotFoundException;
import com.jm.online_store.exception.ExceptionConstants;
import com.jm.online_store.model.Customer;
import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.SentStockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class ManagerReportsRestControllerTest {
    private MockMvc mockMvc;
    private CustomerService customerService;
    private SentStockService sentStockService;
    private ModelMapper modelMapper;
    private final static String END_POINT = "/api/manager";
    private List<Customer> customerList;
    private Map<LocalDate, Long> testMap;
    private static String dayOfWeek;

    @BeforeEach
    void setUp() {
        customerService = mock(CustomerService.class);
        sentStockService = mock(SentStockService.class);
        modelMapper = new ModelMapper();
        testMap = new HashMap<>();
        dayOfWeek = "MONDAY";
        customerList = Arrays.asList(
                new Customer(1L, "customer1@mail.com", DayOfWeekForStockSend.MONDAY),
                new Customer(2L, "customer2@mail.com", DayOfWeekForStockSend.MONDAY),
                new Customer(3L, "customer3@mail.com", DayOfWeekForStockSend.MONDAY));
        testMap.put(LocalDate.now(), 1L);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ManagerReportsRestController(customerService, sentStockService, modelMapper))
                .setControllerAdvice(new ExceptionsHandler())
                .build();
    }

    @Test
    @DisplayName("get all users by day of week")
    void allUsersByDayOfWeek() throws Exception {
        when(customerService.findByDayOfWeekForStockSend(anyString())).thenReturn(customerList);
        mockMvc.perform(get(END_POINT + "/users/{dayOfWeek}", dayOfWeek)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(customerList.size())))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        customerList.get(0).getId().intValue(),
                        customerList.get(1).getId().intValue(),
                        customerList.get(2).getId().intValue())))
                .andExpect(jsonPath("$.data[*].email", containsInAnyOrder(
                        customerList.get(0).getEmail(),
                        customerList.get(1).getEmail(),
                        customerList.get(2).getEmail())))
                .andExpect(jsonPath("$.data[*].dayOfWeekForStockSend", containsInAnyOrder(
                        customerList.get(0).getDayOfWeekForStockSend().toString(),
                        customerList.get(1).getDayOfWeekForStockSend().toString(),
                        customerList.get(2).getDayOfWeekForStockSend().toString())));
    }

    /**
     * Тест не работает, приходит ответ с кодом ошибки 406. Причина не известна
     * Тестировать метод необходимо вручную
     */
    @Test
    @DisplayName("find subscriber by email")
    void findSubscriberByEmail() throws Exception {
        List<Customer> customerByEmail = new ArrayList<>();
        customerByEmail.add(new Customer(4L, "customer1@mail.com", DayOfWeekForStockSend.MONDAY));
        String email = customerByEmail.get(0).getEmail();
        when(customerService.findSubscriberByEmail(anyString())).thenReturn(customerByEmail);
        mockMvc.perform(get(END_POINT + "/user/{email}", email)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.id", containsInAnyOrder(customerByEmail.get(0).getId())))
                .andExpect(jsonPath("$.data.email", containsInAnyOrder(customerByEmail.get(0).getEmail())));
    }

    @Test
    @DisplayName("cancel subscription")
    void cancelSubscription() throws Exception {
      doNothing().when(customerService).cancelSubscription(anyLong());
      mockMvc.perform(put(END_POINT + "/cancel/{id}" , 11L)
              .contentType(MediaType.APPLICATION_JSON)
              .accept(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andDo(print());
    }

    @Test
    @DisplayName("get all sent stocks")
    void allSentStocks() throws Exception {
        when(sentStockService.getSentStocksMap(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1))).thenReturn(testMap);
        mockMvc.perform(get(END_POINT + "/report")
                .param("beginDate", LocalDate.now().minusDays(1).toString())
                .param("endDate", LocalDate.now().plusDays(1).toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("get all users by day of week should returns an empty list")
    void allUsersByDayOfWeekShouldReturnEmptyList() throws Exception {
        when(customerService.findByDayOfWeekForStockSend(anyString())).thenReturn(Collections.emptyList());
        mockMvc.perform(get(END_POINT + "/users/{dayOfWeek}", dayOfWeek))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("find subscriber by email should returns an empty list")
    void findSubscriberByEmailShouldReturnEmptyList() throws Exception {
        when(customerService.findSubscriberByEmail(anyString())).thenReturn(Collections.emptyList());
        mockMvc.perform(get(END_POINT + "/users/{dayOfWeek}", dayOfWeek))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("cancel subscription throws CustomerNotFoundException")
    void cancelSubscriptionThrowsNotFoundException() throws Exception {
        doThrow(new CustomerNotFoundException(ExceptionConstants.NOT_FOUND)).when(customerService).cancelSubscription(anyLong());
        mockMvc.perform(put(END_POINT + "/cancel/{id}" , 11L)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("get all sent stocks returns an empty map")
    void allSentStocksShouldReturnEmptyMap() throws Exception {
        when(sentStockService.getSentStocksMap(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1))).thenReturn(Collections.emptyMap());
        mockMvc.perform(get(END_POINT + "/report")
                .param("beginDate", LocalDate.now().minusDays(1).toString())
                .param("endDate", LocalDate.now().plusDays(1).toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
