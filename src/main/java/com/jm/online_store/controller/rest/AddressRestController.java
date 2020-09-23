package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Address;
import com.jm.online_store.model.Stock;
import com.jm.online_store.service.interf.AddressService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Рест контроллер для адресов
 */
@AllArgsConstructor
@RequestMapping("/customer")
@RestController
public class AddressRestController {
    private final AddressService addressService;

    @GetMapping(value = "/rest/allShops")
    public ResponseEntity<List<Address>> findAll() {
        return ResponseEntity.ok(addressService.findAllShops());
    }
}
