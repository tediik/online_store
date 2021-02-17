package com.jm.online_store.controller.rest.manager;

import com.jm.online_store.model.Address;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.AddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

@PreAuthorize("hasAuthority('ROLE_MANAGER')")
@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/api/manager/shops")
@Api(description = "REST controller for address in manager cabinet")
public class AddressManagerRestController {

    private final AddressService addressService;

    /**
     * Контроллер для отображения адресов всех магазинов
     * @return ResponseEntity<ResponseDto<Address>>(ResponseDto, HttpStatus)
     */
    @GetMapping
    @ApiOperation(value = "Возвращает список всех адресов", authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<List<Address>>> allShops() {
        List<Address> allAddress = addressService.findAllShops();
        if (allAddress.size() == 0) {
            log.debug("Адреса магазинов не найдены");
            return new ResponseEntity<>(new ResponseDto<>(false, "Адреса магазинов не найдены"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseDto<>(true, allAddress), HttpStatus.OK);
    }

    /**
     * Контроллер для отображения адреса по id
     * @id - адрес id (Long)
     * @return ResponseEntity<>(address, HttpStatus.OK)
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "Возвращает адрес по id", authorizations = { @Authorization(value="jwtToken") })
    public ResponseEntity< Address> getAddressInfo(@PathVariable Long id) {
        if(addressService.findAddressById(id).isEmpty()) {
            log.debug("Адрес с id: {} не найден", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        Address address = addressService.findAddressById(id).get();
        return new ResponseEntity<>(address, HttpStatus.OK);
    }

    /**
     * Контроллер для изменения адреса
     */
    @PutMapping
    public ResponseEntity<ResponseDto> editAddress(@RequestBody Address address) {
        addressService.addAddress(address);
        return ResponseEntity.ok().build();
    }

    /**
     * Контроллер для добавления адреса
     * @param newAddress
     * @return
     */
    @PostMapping
    public ResponseEntity addAddress(@RequestBody Address newAddress) {
        addressService.addAddress(newAddress);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Контроллер для удаления адреса по id
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Address> deleteAddress(@PathVariable Long id) {
        addressService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
