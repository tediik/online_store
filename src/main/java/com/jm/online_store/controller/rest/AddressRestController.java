package com.jm.online_store.controller.rest;

import com.jm.online_store.exception.AddressNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.Address;
import com.jm.online_store.model.Stock;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.AddressService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Рест контроллер для адресов
 */
@AllArgsConstructor
@RequestMapping("/customer")
@RestController
public class AddressRestController {
    private final AddressService addressService;
    private final UserService userService;

    @GetMapping(value = "/rest/allShops")
    public ResponseEntity<List<Address>> findAll() {
        return ResponseEntity.ok(addressService.findAllShops());
    }
    @GetMapping(value = "/rest/userAddresses")
    public ResponseEntity<Set<Address>> userAddresses(@AuthenticationPrincipal User user) {
        if(userService.findById(user.getId()).get().getUserAddresses() != null) {
            return ResponseEntity.ok(userService.findById(user.getId()).get().getUserAddresses());
        }
        return ResponseEntity.notFound().build();
    }
    @PostMapping(value = "/rest/addAddress")
    public ResponseEntity addAddressToUser(@AuthenticationPrincipal User user, @RequestBody Address address) {
        if(userService.addNewAddressForUser(user,address)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body("addressIsExists");
    }

    @ExceptionHandler({AddressNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity handleControllerExceptions() {
        return ResponseEntity.notFound().build();
    }
}