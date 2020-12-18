package com.jm.online_store.service.impl;

import com.jm.online_store.exception.CharacteristicNotFoundException;
import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.model.Characteristic;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.ProductCharacteristic;
import com.jm.online_store.repository.ProductCharacteristicRepository;
import com.jm.online_store.repository.ProductRepository;
import com.jm.online_store.service.interf.CharacteristicService;
import com.jm.online_store.service.interf.ProductCharacteristicService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ProductCharacteristicServiceImpl implements ProductCharacteristicService {

    ProductCharacteristicRepository productCharacteristicRepository;
    CharacteristicService characteristicService;
    ProductRepository productRepository;

    @Override
    @Transactional
    public Long addProductCharacteristic(long productId, long characteristicId, String value) {

        Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
        Characteristic characteristic = characteristicService.findCharacteristicById(characteristicId).orElseThrow(CharacteristicNotFoundException::new);

        return productCharacteristicRepository.save(new ProductCharacteristic(product, characteristic, value)).getId();
    }

}
