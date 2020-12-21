package com.jm.online_store.repository;

import com.jm.online_store.model.ProductCharacteristic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCharacteristicRepository extends JpaRepository<ProductCharacteristic, Long> {

}
