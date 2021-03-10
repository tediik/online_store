package com.jm.online_store.controller.rest;

import com.jm.online_store.exception.AddressNotFoundException;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.Address;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.AddressDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.AddressService;
import com.jm.online_store.service.interf.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
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
    private final ModelMapper modelMapper;

    @GetMapping(value = "/allShops")
    @ApiOperation(value = "get all the shops")
    public ResponseEntity<ResponseDto<List<AddressDto>>> findAll() {
        List<AddressDto> addressDto = new ArrayList<>();
        for (Address address : addressService.findAllShops()) {
            addressDto.add(modelMapper.map(address, AddressDto.class));
        }
        return new ResponseEntity<>(new ResponseDto<>(true, addressDto), HttpStatus.OK);
        //.ok(addressService.findAllShops());
    }

    @GetMapping(value = "/userAddresses")
    @ApiOperation(value = "get current logged in Users address")
    public ResponseEntity<ResponseDto<Set<AddressDto>>> userAddresses() {
        Set<AddressDto> addressDto = new HashSet<>();
        for (Address address : userService.getCurrentLoggedInUser().getUserAddresses()) {
            addressDto.add(modelMapper.map(address, AddressDto.class));
        }
        if (userService.getCurrentLoggedInUser().getUserAddresses() != null) {
            return new ResponseEntity<>(new ResponseDto<>(true,
                    addressDto), HttpStatus.OK);
                    //.ok(userService.getCurrentLoggedInUser().getUserAddresses());
        }
        return new ResponseEntity<>(new ResponseDto<>(false, "Addresses not found"), HttpStatus.NOT_FOUND);
                //.notFound().build();
    }

    @PostMapping(value = "/addAddress")
    @ApiOperation(value = "adds address for current logged in user")
    public ResponseEntity<ResponseDto<AddressDto>> addAddressToUser(@RequestBody Address address) {
        User user = userService.getCurrentLoggedInUser();
        if (userService.addNewAddressForUser(user, address)) {
            return new ResponseEntity<>(new ResponseDto<>(true, modelMapper.map(address, AddressDto.class)), HttpStatus.OK);
            //return ResponseEntity.ok().build();
        }
        return new ResponseEntity<>(new ResponseDto<>(false,"addressIsExists"),HttpStatus.BAD_REQUEST);
        //return ResponseEntity.badRequest().body("addressIsExists");
    }

    @ExceptionHandler({AddressNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<ResponseDto<?>> handleControllerExceptions() {
        return new ResponseEntity<>(new ResponseDto<>(false, "Not found"), HttpStatus.NOT_FOUND);
        //return ResponseEntity.notFound().build();
    }
}
