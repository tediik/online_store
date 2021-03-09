package com.jm.online_store.controller.rest.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jm.online_store.controller.ExceptionsHandler;
import com.jm.online_store.exception.AddressNotFoundException;
import com.jm.online_store.model.Address;
import com.jm.online_store.service.interf.AddressService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

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


class ManagerAddressRestControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private AddressService addressService;
    private ArrayList<Address> shopList ;
    private Address address1;
    private Address address2;
    private Address address3;
    private static final String END_POINT = "/api/manager/shops";

    @AfterEach

    @BeforeEach
    void setUp() {
        addressService = mock(AddressService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new ManagerAddressRestController(addressService))
                .setControllerAdvice(new ExceptionsHandler()).build();
        objectMapper = new ObjectMapper();
        shopList = new ArrayList<>();
        address1 = new Address( 1L, "Republic Korelia", "Petrozavodsk" , "street" , "1c3", "3", "333", true);
        address2 = new Address(2L,  "Московская область", "Москва", "Ленина", "2c4","4","444", true);
        address3 = new Address(3L,  "Томбовская область", "Тамбов", "Запорожская", "3c5", "5" ,"555" ,false);
        shopList.add(address1);
        shopList.add(address2);
        shopList.add(address3);
    }

    @Test
    @DisplayName("edit address")
    void editAddress() throws Exception {
        Address address4 = new Address(1L, "Volgogradskaya oblast", "Volgograd", "Red street", "4c6", "5", "666", false);
        when(addressService.editAddress(address1)).thenReturn(address4);
        mockMvc.perform(put(END_POINT)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(address4)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(address4.getId()))
                .andExpect(jsonPath("$.data.region").value(address4.getRegion()))
                .andExpect(jsonPath("$.data.city").value(address4.getCity()));

    }

    @Test
    @DisplayName("find all address shops")
    void getAllShops() throws Exception {
        when(addressService.findAllShopsManager()).thenReturn(shopList);
        mockMvc.perform(get(END_POINT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        address1.getId().intValue(),
                        address2.getId().intValue(),
                        address3.getId().intValue())))
                .andExpect(jsonPath("$.data[*].region", containsInAnyOrder(
                        address1.getRegion(),
                        address2.getRegion(),
                        address3.getRegion())))
                .andExpect(jsonPath("$.data[*].city", containsInAnyOrder(
                        address1.getCity(),
                        address2.getCity(),
                        address3.getCity())));

    }

    @Test
    @DisplayName("add address")
    void addAddress() throws Exception {
        when(addressService.addAddress(any(Address.class))).thenReturn(address2);
        mockMvc.perform(post(END_POINT)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(address2)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(address2.getId()))
                .andExpect(jsonPath("$.data.region").value(address2.getRegion()))
                .andExpect(jsonPath("$.data.city").value(address2.getCity()));

    }

    @Test
    @DisplayName("get address info")
    void getAddressInfo() throws Exception {
        when(addressService.findAddressById(anyLong())).thenReturn(Optional.ofNullable(address1));
        mockMvc.perform(get(END_POINT + "/{id}", 11)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.region").value(address1.getRegion()))
                .andExpect(jsonPath("$.data.city").value(address1.getCity()))
                .andExpect(jsonPath("$.data.street").value(address1.getStreet()))
                .andExpect(jsonPath("$.data.building").value(address1.getBuilding()))
                .andExpect(jsonPath("$.data.zip").value(address1.getZip()))
                .andExpect(jsonPath("$.data.shop").value(address1.isShop()));
    }


    @Test
    @DisplayName("address shop list return empty data")
    void shouldReturnEmptyList() throws Exception {
        when(addressService.findAllShopsManager()).thenReturn(Collections.emptyList());
        mockMvc.perform(get(END_POINT))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("delete address by id")
    void deleteAddress() throws Exception {
        when(addressService.findAddressById(anyLong())).thenReturn(Optional.ofNullable(address3));
        mockMvc.perform(delete(END_POINT + "/{id}", 11)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("throws address not found exception when try to delete address by id")
    void deleteAddressThrowsAddressNotFoundException() throws Exception {
        when(addressService.findAddressById(anyLong())).thenThrow(new AddressNotFoundException());
        mockMvc.perform(delete(END_POINT + "{id}", 11))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("throws address not found exception when try to get address info by id")
    void getAddressInfoThrowsAddressNotFoundException() throws Exception {
        when(addressService.findAddressById(anyLong())).thenThrow(new AddressNotFoundException());
        mockMvc.perform(get(END_POINT + "/{id}", 11)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
