package com.jm.online_store.controller.rest;

import com.jm.online_store.enums.Response;
import com.jm.online_store.exception.AddressNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
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
     * @return ResponseEntity<ResponseDto <Set<Address>>>(ResponseDto, HttpStatus) {@link ResponseEntity}
     */
    @GetMapping(value = "/customerAddresses")
    @ApiOperation(value = "get current logged in Users address")
    public ResponseEntity<ResponseDto<Set<Address>>> customerAddresses() {
        Set<Address> userAddress =  customerService.getCurrentLoggedInCustomer().getUserAddresses();
        if (userAddress.isEmpty()) {
            return new ResponseEntity<>(new ResponseDto<>(false, "User address not found"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(new ResponseDto<>(true, userAddress));
    }

    /**
     * Метод для добавления адреса магазина пользователем
     *
     * @param address - адрес пользователя {@link Address}
     * @return ResponseEntity<ResponseDto < Address>>(ResponseDto, HttpStatus) {@link ResponseEntity}
     */
    @PostMapping(value = "/addAddress")
    @ApiOperation(value = "adds address for current logged in user")
    public ResponseEntity<ResponseDto<String>> addAddressToCustomer(@RequestBody Address address) {
        Customer customer = customerService.getCurrentLoggedInCustomer();
        return customer != null && customerService.addNewAddressForCustomer(customer, address) ?
                ResponseEntity.ok(new ResponseDto<>(true, Response.SAVED.name(), Response.NO_ERROR.getText())) :
                new ResponseEntity<>(new ResponseDto<>(false, Response.FAILED.name()), HttpStatus.BAD_REQUEST);

    }
//        if (customer != null) {
//            if (customerService.addNewAddressForCustomer(customer, address)) {
//                return ResponseEntity.ok(new ResponseDto<>(true, Response.SAVED.name(), Response.NO_ERROR.getText()));
//            }
//        }
//        return new ResponseEntity<>(new ResponseDto<>(false, Response.FAILED.name()), HttpStatus.BAD_REQUEST);

    @ExceptionHandler({AddressNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<?> handleControllerExceptions() {
        return ResponseEntity.notFound().build();
    }
}
