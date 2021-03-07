package com.jm.online_store.service.impl;

import com.jm.online_store.enums.ExceptionEnums;
import com.jm.online_store.exception.AddressNotFoundException;
import com.jm.online_store.exception.constants.ExceptionConstants;
import com.jm.online_store.model.Address;
import com.jm.online_store.repository.AddressRepository;
import com.jm.online_store.service.interf.AddressService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;

    /**
     * Метод поиска адреса магазина по id.
     * @param idAddress - id адреса.
     * @return Optional<Address>
     * @throws AddressNotFoundException - NotFoundException
     */
    @Override
    public Optional<Address> findAddressById(Long idAddress) {
        Optional<Address> address = addressRepository.findById(idAddress);
        if(address.isEmpty()) {
            log.debug("Address with id: {} not found.", idAddress);
            throw new AddressNotFoundException(ExceptionEnums.ADDRESS.getText() + String.format(ExceptionConstants.WITH_SUCH_ID_NOT_FOUND, idAddress));
        } else {
            return address;
        }
    }

    /**
     * Метод для добавления адреса магазина.
     * @param address - адрес магазина {@link Address}
     * @return Address
     */
    @Override
    public Address addAddress(Address address) {
        return addressRepository.save(address);
    }

    /**
     * Метод для изменения адреса магазина.
     * @param address - адрес магазина {@link Address}
     * @return Address
     * @throws AddressNotFoundException - NotFoundException
     */
    @Override
    public Address editAddress(Address address) {
        if(addressRepository.findById(address.getId()).isEmpty()) {
            throw new AddressNotFoundException(ExceptionEnums.ADDRESS.getText() + String.format(ExceptionConstants.WITH_SUCH_ID_NOT_FOUND, address.getId()));
        }
        log.info("Address with id: {} updated successfully.", address.getId());
        return addressRepository.save(address);
    }

    /**
     * Метод поиска активных адресов магазинов доступных кастомеру.
     * @return List<Address> - список всех адресов магазинов, доступных кастомеру.
     * @throws AddressNotFoundException - NotFoundException
     */
    @Override
    public List<Address> findAllShops() {
        List<Address> shops = addressRepository.findAllByShopIsTrue();
        if (shops.isEmpty()) {
            throw new AddressNotFoundException(ExceptionEnums.ADDRESS.getText() + ExceptionConstants.NOT_FOUND);
        }
        return shops;
    }

    /**
     * Метод поиска всех адресов магазинов.
     * @return List<Address> - список всех адресов магазинов.
     * @throws AddressNotFoundException - NotFoundException
     */
    @Override
    public List<Address> findAllShopsManager() {
        List<Address> shops = addressRepository.findAll();
        if (shops.isEmpty()) {
            throw new AddressNotFoundException(ExceptionEnums.ADDRESS.getText() + String.format(ExceptionConstants.NOT_FOUND));
        }
        return shops;
    }

    /**
     * Метод для удаления адреса по id.
     * @param id - id адреса
     * @throws AddressNotFoundException - NotFoundException
     */
    @Override
    public void deleteById(Long id) {
        try {
            addressRepository.deleteById(id);
            log.debug("Address with id: {}, was deleted successfully.", id);
        } catch (IllegalArgumentException | EmptyResultDataAccessException | NullPointerException e) {
            throw new AddressNotFoundException(ExceptionEnums.ADDRESS.getText() + String.format(ExceptionConstants.WITH_SUCH_ID_NOT_FOUND, id));
        }
    }

    /**
     * Метод поиска адреса, совпадающего по всем полям.
     * @param address - адрес магазина {@link Address}
     * @return Optional<Address>
     */
    @Override
    public Optional<Address> findSameAddress(Address address) {
        return Optional.ofNullable(addressRepository.findAddressByRegionAndFlatAndCityAndStreetAndBuildingAndZip(
                address.getRegion(),
                address.getFlat(),
                address.getCity(),
                address.getStreet(),
                address.getBuilding(),
                address.getZip()
        ));
    }
}
