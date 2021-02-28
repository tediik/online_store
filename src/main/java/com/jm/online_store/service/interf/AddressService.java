package com.jm.online_store.service.interf;

import com.jm.online_store.model.Address;

import java.util.List;
import java.util.Optional;

public interface AddressService {

    List<Address> findAllShops();
    List<Address> findAllShopsManager();
    Optional<Address> findAddressById(Long idAddress);
    Address addAddress(Address address);
    Address editAddress(Address address);
    Optional<Address> findSameAddress(Address address);
    void deleteById(Long id);
}
