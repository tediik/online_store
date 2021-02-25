package com.jm.online_store.service.impl;

import com.jm.online_store.exception.AddressNotFoundException;
import com.jm.online_store.model.Address;
import com.jm.online_store.repository.AddressRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AddressServiceImplTest {
    @Mock
    private AddressRepository addressRepository;
    @InjectMocks
    AddressServiceImpl addressService;

    List<Address> shopList;
    Address address1;
    Address address2;
    Address address3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        shopList = new ArrayList<>();
        address1 = new Address("420077", "Татарстан", "Казань", "Революционная", "2", "25", true);
        address2 = new Address("420078", "Московская область", "Москва", "Ленина", "2", "126", true);
        address3 = new Address("420079", "Тамбовская область", "Тамбов", "Запорожская", "2", "11", false);
        shopList.add(address1);
        shopList.add(address2);
    }
    @AfterEach
    void tearDown() {
        address1 = null;
        address2 = null;
        address3 = null;
        shopList = null;
    }
    @Test
    void findAllShops() {
        when(addressRepository.findAllByShopIsTrue()).thenReturn(shopList);
        assertEquals(addressService.findAllShops(),shopList);
    }
    @Test
    void findAllShopsThrowsException() {
        when(addressRepository.findAllByShopIsTrue()).thenReturn(new ArrayList<>());
        assertThrows(AddressNotFoundException.class,()->addressService.findAllShops());
    }
    @Test
    void addAddress() {
        when(addressRepository.save(address1)).thenReturn(address1);
        Address testAddress = addressService.addAddress(address1);
        assertEquals(address1.getBuilding(),testAddress.getBuilding());
        assertEquals(address1.getCity(),testAddress.getCity());
        assertEquals(address1.getRegion(),testAddress.getRegion());
        assertEquals(address1.getZip(),testAddress.getZip());
        assertEquals(address1.getStreet(),testAddress.getStreet());
        assertEquals(address1.isShop(),testAddress.isShop());
        verify(addressRepository, times(1)).save(address1);
    }

    @Test
    void findAddressById() {
        address2.setId(2L);
        when(addressRepository.findById(2L)).thenReturn(Optional.ofNullable(address2));
        Optional<Address> testAddress = addressService.findAddressById(2L);
        assertNotNull(testAddress.get());
        assertEquals(testAddress.get().getId(),address2.getId());
        verify(addressRepository, times(1)).findById(2L);
    }
    @Test
    void findSameAdress() {
        Address testAddress = new Address("420079", "Тамбовская область", "Тамбов", "Запорожская", "2", "11", false);
        when(addressRepository.findAddressByRegionAndFlatAndCityAndStreetAndBuildingAndZip(
                address3.getRegion(),
                address3.getFlat(),
                address3.getCity(),
                address3.getStreet(),
                address3.getBuilding(),
                address3.getZip())).thenReturn(address3);
        assertEquals(testAddress,address3);
    }
}
