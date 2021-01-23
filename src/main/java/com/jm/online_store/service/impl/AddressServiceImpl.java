package com.jm.online_store.service.impl;

import com.jm.online_store.exception.AddressNotFoundException;
import com.jm.online_store.model.Address;
import com.jm.online_store.repository.AddressRepository;
import com.jm.online_store.service.interf.AddressService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;

    /**
     * Метод поиска адреса по id
     * @param idAddress
     * @return Optional<Address>
     */
    @Override
    public Optional<Address> findAddressById(Long idAddress) {
        return addressRepository.findById(idAddress);
    }

    /**
     * Метод добавления адреса
     * @param address
     * @return Address
     */
    @Override
    public Address addAddress(Address address) {
        return addressRepository.save(address);
    }

    /**
     * Метод поиска адресов магазинов
     * @return List<Address>
     * @throws AddressNotFoundException
     */
    @Override
    public List<Address> findAllShops() {
        List<Address> shops = addressRepository.findAllByShopIsTrue();
        if(shops.isEmpty()) {
            throw new AddressNotFoundException();
        }
        return shops;
    }

    /**
     * Метод поиска адресa, совпадающего по всем полям
     * @return Optional<Address>
     */
    @Override
    public Optional<Address> findSameAddress(Address address) {
        return Optional.ofNullable(addressRepository.findAddressByRegionAndDistrictAndCityAndStreetAndBuildingAndFlatAndZip(
                address.getRegion(),
                address.getDistrict(),
                address.getCity(),
                address.getStreet(),
                address.getBuilding(),
                address.getFlat(),
                address.getZip()
        ));
    }
}