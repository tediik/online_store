package com.jm.online_store.controller.rest.manager;

import com.jm.online_store.model.Address;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.AddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Manager Rest controller to work with shops
 */
@PreAuthorize("hasAuthority('ROLE_MANAGER')")
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/api/manager/shops")
@Api(value = "REST controller for address in manager cabinet")
public class ManagerAddressRestController {

    private final AddressService addressService;

    /**
     * Метод для отображения адресов всех магазинов
     * @return ResponseEntity<ResponseDto<Address>>(ResponseDto, HttpStatus) {@link ResponseEntity}
     */
    @GetMapping()
    @ApiOperation(value = "receive all address from db", authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "returns empty list"),
            @ApiResponse(code = 200, message = "Ok")
    })
    public ResponseEntity<ResponseDto<List<Address>>> allShops() {
        List<Address> addressList = addressService.findAllShopsManager();
        return new ResponseEntity<>(new ResponseDto<>(true, addressList), HttpStatus.OK);
    }

    /**
     * Метод для отображения адреса магазина по id.
     * @param id - адрес id {@link Long}
     * @return ResponseEntity<ResponseDto<List<Address>>>(ResponseDto, HttpStatus) {@link ResponseEntity}
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "receive address by id from db", authorizations = { @Authorization(value="jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Address not found"),
            @ApiResponse(code = 200, message = "Ok")
    })
    public ResponseEntity<ResponseDto<Address>> getAddressInfo(@PathVariable Long id) {
        Address address = addressService.findAddressById(id).orElseThrow();
        return new ResponseEntity<>(new ResponseDto<>(true, address), HttpStatus.OK);
    }

    /**
     * Метод для изменения адреса магазина.
     * @param address - адрес магазина {@link Address}
     * @return ResponseEntity<ResponseDto<Address>>(ResponseDto, HttpStatus) {@link ResponseEntity}
     */
    @PutMapping
    @ApiOperation(value = "modify address from manager page", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ResponseDto<Address>> editAddress(@RequestBody Address address) {
        addressService.editAddress(address);
        return new ResponseEntity<>(new ResponseDto<>(true, address), HttpStatus.OK);
    }

    /**
     * Метод для добавления нового адреса магазина.
     * @param newAddress - новый адрес магазина {@link Address}
     * @return ResponseEntity<ResponseDto<Address>>(ResponseDto, HttpStatus) {@link ResponseEntity}
     */
    @PostMapping
    @ApiOperation(value = "add new address from manager page", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity<ResponseDto<Address>> addAddress(@RequestBody Address newAddress) {
        addressService.addAddress(newAddress);
        return new ResponseEntity<>(new ResponseDto<>(true, newAddress), HttpStatus.CREATED);
    }

    /**
     * Метод для удаления адреса магазина по id.
     * @param id - адрес id
     * @return ResponseEntity<ResponseDto<Address>>(ResponseDto, HttpStatus) {@link ResponseEntity}
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "delete address by id from manager page", authorizations = { @Authorization(value="jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "ADDRESS NOT FOUND"),
            @ApiResponse(code = 200, message = ""),
    })
    public ResponseEntity<ResponseDto<Address>> deleteAddress(@PathVariable Long id) {
        Address address = addressService.findAddressById(id).orElse(null);
        addressService.deleteById(id);
        return new ResponseEntity<>(new ResponseDto<>(true, address), HttpStatus.OK);
    }
}
