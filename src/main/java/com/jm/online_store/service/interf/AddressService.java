package com.jm.online_store.service.interf;

import com.jm.online_store.model.Address;

import java.util.List;
import java.util.Optional;

public interface AddressService {
    Optional<Address> findAddressById(Long idAddress);
    Address addAddress(Address address);
    List<Address> findAllShops();
}
