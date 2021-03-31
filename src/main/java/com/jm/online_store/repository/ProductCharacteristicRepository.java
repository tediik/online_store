package com.jm.online_store.repository;

import com.jm.online_store.model.Characteristic;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.ProductCharacteristic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCharacteristicRepository extends JpaRepository<ProductCharacteristic, Long> {

    ProductCharacteristic findProductCharacteristicsByValueAndCharacteristicAndProduct(String value, Characteristic characteristic, Product product);

    List<ProductCharacteristic> findProductCharacteristicsByValueAndCharacteristic(String value, Characteristic characteristic);
}
