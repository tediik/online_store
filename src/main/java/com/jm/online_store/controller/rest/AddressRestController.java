package com.jm.online_store.controller.rest;

import com.jm.online_store.exception.AddressNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.Address;
import com.jm.online_store.model.Customer;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.AddressService;
import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * Рест контроллер для адресов
 */
@AllArgsConstructor
@RequestMapping("/api/customer")
@RestController
@Api(description = "Rest controller for addresses")
public class AddressRestController {
    private final AddressService addressService;
    private final UserService userService;
    private final CustomerService customerService;

    @GetMapping(value = "/allShops")
    @ApiOperation(value = "get all the shops")
    public ResponseEntity<List<Address>> findAll() {
        return ResponseEntity.ok(addressService.findAllShops());
    }

    @GetMapping(value = "/userAddresses")
    @ApiOperation(value = "get current logged in Users address")
    public ResponseEntity<Set<Address>> userAddresses() {
        if (customerService.getCurrentLoggedInUser().getUserAddresses() != null) {
            return ResponseEntity.ok(customerService.getCurrentLoggedInUser().getUserAddresses());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping(value = "/addAddress")
    @ApiOperation(value = "adds address for current logged in user")
    public ResponseEntity addAddressToCustomer(@RequestBody Address address) {
        Customer customer = customerService.getCurrentLoggedInUser();
        if (customerService.addNewAddressForCustomer(customer, address)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body("addressIsExists");
    }

    @ExceptionHandler({AddressNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity handleControllerExceptions() {
        return ResponseEntity.notFound().build();
    }
}
