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
     * Метод поиска адреса по id.
     * @param idAddress {@link Long}
     * @return Optional<Address>
     */
    @Override
    public Optional<Address> findAddressById(Long idAddress) {
        return addressRepository.findById(idAddress);
    }

    /**
     * Метод добавления адреса.
     * @param address {@link Address}
     * @return Address
     */
    @Override
    public Address addAddress(Address address) {
        return addressRepository.save(address);
    }

    /**
     * Метод поиска активных адресов магазинов.
     * @return List<Address>
     * @throws AddressNotFoundException
     */
    @Override
    public List<Address> findAllShops() {
        List<Address> shops = addressRepository.findAllByShopIsTrue();
        if (shops.isEmpty()) {
            throw new AddressNotFoundException();
        }
        return shops;
    }

    /**
     * Метод поиска всех адресов магазинов.
     *
     */
    @Override
    public List<Address> findAllShopsManager() {
        Address address = new Address();
        if(address.isShop()) {
            return addressRepository.findAllByShopIsTrue();
        }
        return addressRepository.findAllByShopIsTrue();
    }

    /**
     * Метод для удаления адреса по id.
     */
    @Override
    public void deleteById(Long id) {
        addressRepository.deleteById(id);
    }

    /**
     * Метод поиска адреса, совпадающего по всем полям.
     * @param address {@link Address}
     * @return Optional<Address>
     */
    @Override
    public Optional<Address> findSameAddress(Address address) {
        return Optional.ofNullable(addressRepository.findAddressByRegionAndDistrictAndCityAndStreetAndBuildingAndZip(
                address.getRegion(),
                address.getDistrict(),
                address.getCity(),
                address.getStreet(),
                address.getBuilding(),
                address.getZip()
        ));
    }
}