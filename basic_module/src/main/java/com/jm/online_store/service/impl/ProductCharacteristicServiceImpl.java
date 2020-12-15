package com.jm.online_store.service.impl;

import com.jm.online_store.model.Characteristic;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.ProductCharacteristic;
import com.jm.online_store.repository.ProductCharacteristicRepository;
import com.jm.online_store.service.interf.CharacteristicService;
import com.jm.online_store.service.interf.ProductCharacteristicService;
import com.jm.online_store.service.interf.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ProductCharacteristicServiceImpl implements ProductCharacteristicService {

    ProductCharacteristicRepository productCharacteristicRepository;
    ProductService productService;
    CharacteristicService characteristicService;

    @Override
    @Transactional
    public Long addProductCharacteristic(long productId, long characteristicId, String value) {

        Product product = productService.findProductById(productId).get();
        Characteristic characteristic = characteristicService.findCharacteristicById(characteristicId).get();

        return productCharacteristicRepository.save(new ProductCharacteristic(product, characteristic, value)).getId();
    }

}
