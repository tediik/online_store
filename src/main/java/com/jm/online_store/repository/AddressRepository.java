package com.jm.online_store.repository;

import com.jm.online_store.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAllByShopIsTrue();
    Address findAddressByRegionAndFlatAndCityAndStreetAndBuildingAndZip(String region,
                                                                                            String flat,
                                                                                            String city,
                                                                                            String street,
                                                                                            String building,
                                                                                            String zip);
}
