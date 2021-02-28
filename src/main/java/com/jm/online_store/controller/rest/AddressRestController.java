package com.jm.online_store.controller.rest;

import com.jm.online_store.model.Address;
import com.jm.online_store.model.Customer;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.AddressService;
import com.jm.online_store.service.interf.CustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@Api(value = "Rest controller for addresses")
public class AddressRestController {
    private final AddressService addressService;
    private final CustomerService customerService;

    /**
     * Метод для отображения адресов всех активных магазинов
     *
     * @return ResponseEntity<ResponseDto < List < Address>>>(ResponseDto, HttpStatus) {@link ResponseEntity}
     */
    @GetMapping(value = "/allShops")
    @ApiOperation(value = "get all the shops", authorizations = {@Authorization(value = "jwtToken")})
    public ResponseEntity<ResponseDto<List<Address>>> findAll() {
        return new ResponseEntity<>(new ResponseDto<>(true, addressService.findAllShops()), HttpStatus.OK);
    }

    /**
     * Метод для отображения адресов пользователя
     *
     * @return ResponseEntity<ResponseDto < Set < Address>>>(ResponseDto, HttpStatus) {@link ResponseEntity}
     */
    @GetMapping(value = "/customerAddresses")
    @ApiOperation(value = "get current logged in Users address")
    public ResponseEntity<Set<Address>> customerAddresses() {
        if (customerService.getCurrentLoggedInCustomer().getUserAddresses() != null) {
            return ResponseEntity.ok(customerService.getCurrentLoggedInCustomer().getUserAddresses());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Метод для добавления адреса магазина пользователем
     *
     * @param address - адрес пользователя {@link Address}
     * @return ResponseEntity<ResponseDto < Address>>(ResponseDto, HttpStatus) {@link ResponseEntity}
     */
    @PostMapping(value = "/addAddress")
    @ApiOperation(value = "adds address for current logged in user")
    public ResponseEntity addAddressToCustomer(@RequestBody Address address) {
        Customer customer = customerService.getCurrentLoggedInCustomer();
        if (customerService.addNewAddressForCustomer(customer, address)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body("addressIsExists");
    }
}
