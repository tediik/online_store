package com.jm.online_store.service.impl;

import com.jm.online_store.exception.CharacteristicNotFoundException;
import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.model.Characteristic;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.ProductCharacteristic;
import com.jm.online_store.model.dto.ProductCharacteristicDto;
import com.jm.online_store.repository.ProductCharacteristicRepository;
import com.jm.online_store.repository.ProductRepository;
import com.jm.online_store.service.interf.CharacteristicService;
import com.jm.online_store.service.interf.ProductCharacteristicService;
import com.jm.online_store.service.interf.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ProductCharacteristicServiceImpl implements ProductCharacteristicService {

    ProductCharacteristicRepository productCharacteristicRepository;
    CharacteristicService characteristicService;
    ProductRepository productRepository;

    /**
     * Метод добавления ProductCharacteristic, который соотносит харкетристики и их значения с товарами
     *
     * @param productId идентификатор товара, к которому добавляем характеристику
     * @param characteristicId идентификатор характеристики, значение которой добавляем товару
     * @param value значение характеристики, которую добавляем товару
     * @return Long id - идентификатор ProductCharacteristic
     */
    @Override
    @Transactional
    public Long addProductCharacteristic(long productId, long characteristicId, String value) {

        Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
        Characteristic characteristic = characteristicService.findCharacteristicById(characteristicId).orElseThrow(CharacteristicNotFoundException::new);

        return productCharacteristicRepository.save(new ProductCharacteristic(product, characteristic, value)).getId();
    }

    /**
     * Метод добавления ProductCharacteristic, который соотносит харкетристики и их значения с товарами
     *
     * @param productId идентификатор товара, к которому добавляем характеристику
     * @param characteristicName наименование характеристики, значение которой добавляем товару
     * @param value значение характеристики, которую добавляем товару
     * @return Long id - идентификатор ProductCharacteristic
     */
    @Override
    @Transactional
    public ProductCharacteristic addProductCharacteristic(long productId, String characteristicName, String value) {

        Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
        Characteristic characteristic = characteristicService.findByCharacteristicName(characteristicName).orElseThrow(CharacteristicNotFoundException::new);

        return productCharacteristicRepository.save(new ProductCharacteristic(product, characteristic, value));
    }

    @Override
    @Transactional
    public List<ProductCharacteristic> addProductCharacteristic(List<ProductCharacteristicDto> list, String addedProductName) {
        List<ProductCharacteristic> returnValue = new ArrayList<>();
        list.forEach(s -> {
            Characteristic characteristic = characteristicService.getCharacteristicById(s.getCharacteristicId());
            Product product = productRepository.findByProduct(addedProductName).orElseThrow(ProductNotFoundException::new);
            returnValue.add(productCharacteristicRepository.save(new ProductCharacteristic(product, characteristic, s.getValue())));
        });
//        for (ProductCharacteristicDto tmp: list) {
//            Characteristic characteristic = characteristicService.getCharacteristicById(tmp.getCharacteristicId());
//            Product product = productRepository.findByProduct(addedProductName).orElseThrow(ProductNotFoundException::new);
//            returnValue.add(productCharacteristicRepository.save(new ProductCharacteristic(product, characteristic, tmp.getValue())));
//        }
        return returnValue;
    }
}
