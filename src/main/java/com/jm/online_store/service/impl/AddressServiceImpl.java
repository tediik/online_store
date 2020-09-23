package com.jm.online_store.service.impl;

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

    @Override
    public Optional<Address> findAddressById(Long idAddress) {
        return addressRepository.findById(idAddress);
    }

    @Override
    public Address addAddress(Address address) {
        return addressRepository.save(address);
    }

    @Override
    public List<Address> findAllShops() {
        return addressRepository.findAllByShopIsTrue();
    }
}
