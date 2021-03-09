package com.jm.online_store.controller.rest.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jm.online_store.config.filters.CorsFilter;
import com.jm.online_store.controller.ExceptionsHandler;
import com.jm.online_store.enums.DayOfWeekForStockSend;
import com.jm.online_store.model.Customer;
import com.jm.online_store.model.News;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.CustomerDto;
import com.jm.online_store.model.dto.NewsDto;
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
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class ManagerReportsRestControllerTest {

    private MockMvc mockMvc;
    private CustomerService customerService;
    private SentStockService sentStockService;
    private ModelMapper modelMapper;
    private Type listType;
    private ObjectMapper objectMapper;
    private final static String END_POINT = "/api/manager";
    private List<Customer> customerList;

    @BeforeEach
    void setUp() {
        customerService = mock(CustomerService.class);
        sentStockService = mock(SentStockService.class);
        modelMapper = new ModelMapper();
        listType = new TypeToken<List<CustomerDto>>() {}.getType();
        objectMapper = new ObjectMapper();
        customerList = Arrays.asList(
                new Customer(1L, "customer1@mail.com", DayOfWeekForStockSend.MONDAY),
                new Customer(2L, "customer2@mail.com", DayOfWeekForStockSend.MONDAY),
                new Customer(3L, "customer3@mail.com", DayOfWeekForStockSend.MONDAY));
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ManagerReportsRestController(customerService, sentStockService, modelMapper))
                .build();
    }


    @Test
    @DisplayName("get all users by day of week")
    void allUsersByDayOfWeek() throws Exception {
        String dayOfWeek = "MONDAY";
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
        customerByEmail = Collections.singletonList(new Customer(4L, "customer1@mail.com", DayOfWeekForStockSend.MONDAY));
        String email = customerByEmail.get(0).getEmail();

        when(customerService.findSubscriberByEmail(anyString())).thenReturn(customerByEmail);
        mockMvc.perform(get(END_POINT + "/customer/{email}", email)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.id", containsInAnyOrder(customerByEmail.get(0).getId())))
                .andExpect(jsonPath("$.data.email", containsInAnyOrder(customerByEmail.get(0).getEmail())))
                .andExpect(status().isOk());
    }

    @Test
    void cancelSubscription() {
    }

    @Test
    void allSentStocks() {
    }


    private static CustomerDto toCustomerDto(Customer customer) {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(customer.getId());
        customerDto.setEmail(customer.getEmail());
        customerDto.setDayOfWeekForStockSend(customer.getDayOfWeekForStockSend());
        return customerDto;
    }
}
