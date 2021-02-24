package com.jm.online_store.controller.rest;

import com.jm.online_store.exception.AddressNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.Address;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.AddressService;
import com.jm.online_store.service.interf.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
    private final UserService userService;

    /**
     * Метод для отображения адресов всех активных магазинов
     * @return ResponseEntity<ResponseDto<List<Address>>>(ResponseDto, HttpStatus) {@link ResponseEntity}
     */
    @GetMapping(value = "/allShops")
    @ApiOperation(value = "get all the shops")
    public ResponseEntity<ResponseDto<List<Address>>> findAll() {
        return new ResponseEntity<>(new ResponseDto<>(true, addressService.findAllShops()),HttpStatus.OK);
    }

    /**
     * Метод для отображения адресов пользователя
     * @return ResponseEntity<ResponseDto<Set<Address>>>(ResponseDto, HttpStatus) {@link ResponseEntity}
     */
    @GetMapping(value = "/userAddresses")
    @ApiOperation(value = "get current logged in Users address")
    public ResponseEntity<ResponseDto<Set<Address>>> userAddresses() {
        Set<Address> userAddress =  userService.getCurrentLoggedInUser().getUserAddresses();
        if (userAddress.isEmpty()) {
            return new ResponseEntity<>(new ResponseDto<>(false, "User address not found"), HttpStatus.NOT_FOUND);

        }
        return new ResponseEntity<>(new ResponseDto<>(true, userAddress), HttpStatus.OK);
    }

    /**
     * Метод для добавления адреса магазина пользователем
     * @param address - адрес пользователя {@link Address}
     * @return ResponseEntity<ResponseDto<Address>>(ResponseDto, HttpStatus) {@link ResponseEntity}
     */
    @PostMapping(value = "/addAddress")
    @ApiOperation(value = "adds address for current logged in user")
    public ResponseEntity<ResponseDto<Address>> addAddressToUser(@RequestBody Address address) {
        User user = userService.getCurrentLoggedInUser();
        if (!userService.addNewAddressForUser(user, address)) {
            return new ResponseEntity<>(new ResponseDto<>(false, "Address is exist"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseDto<>(true, address), HttpStatus.OK);
    }

    @ExceptionHandler({AddressNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<?> handleControllerExceptions() {
        return ResponseEntity.notFound().build();
    }
}
