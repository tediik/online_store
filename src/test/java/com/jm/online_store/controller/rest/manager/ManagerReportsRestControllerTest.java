package com.jm.online_store.controller.rest.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jm.online_store.config.filters.CorsFilter;
import com.jm.online_store.controller.ExceptionsHandler;
import com.jm.online_store.enums.DayOfWeekForStockSend;
import com.jm.online_store.exception.CustomerNotFoundException;
import com.jm.online_store.exception.ExceptionConstants;
import com.jm.online_store.model.Customer;
import com.jm.online_store.model.News;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.CustomerDto;
import com.jm.online_store.model.dto.NewsDto;
import com.jm.online_store.repository.CustomerRepository;
import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.NewsService;
import com.jm.online_store.service.interf.SentStockService;
import org.hamcrest.Matchers;
import org.hibernate.type.ListType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class ManagerReportsRestControllerTest {

    private MockMvc mockMvc;
    private CustomerService customerService;
    private CustomerRepository customerRepository;
    private SentStockService sentStockService;
    private ModelMapper modelMapper;
    private Type listType;
    private ObjectMapper objectMapper;
    private final static String END_POINT = "/api/manager";
    private List<Customer> customerList;
    private Map<LocalDate, Long> testMap;
    private static String dayOfWeek;

    @BeforeEach
    void setUp() {
        customerService = mock(CustomerService.class);
        sentStockService = mock(SentStockService.class);
        customerRepository = mock(CustomerRepository.class);
        modelMapper = new ModelMapper();
        listType = new TypeToken<List<CustomerDto>>() {}.getType();
        objectMapper = new ObjectMapper();
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
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(jsonPath("$.data", hasSize(customerList.size())))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        customerList.get(0).getId().intValue(),
                        customerList.get(1).getId().intValue(),
                        customerList.get(2).getId().intValue()
                )))
                .andExpect(jsonPath("$.data[*].email", containsInAnyOrder(
                        customerList.get(0).getEmail(),
                        customerList.get(1).getEmail(),
                        customerList.get(2).getEmail()
                )))
                .andExpect(jsonPath("$.data[*].dayOfWeekForStockSend", containsInAnyOrder(
                        customerList.get(0).getDayOfWeekForStockSend().toString(),
                        customerList.get(1).getDayOfWeekForStockSend().toString(),
                        customerList.get(2).getDayOfWeekForStockSend().toString()
                )))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("find subscriber by email")
    void findSubscriberByEmail() throws Exception {
        List<Customer> customerByEmail = new ArrayList<>();
        customerByEmail.add(new Customer(4L, "customer1@mail.com", DayOfWeekForStockSend.MONDAY));
        String email = customerByEmail.get(0).getEmail();
        when(customerService.findSubscriberByEmail(anyString())).thenReturn(customerByEmail);
        mockMvc.perform(get(END_POINT + "/user/{email}", email)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
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
