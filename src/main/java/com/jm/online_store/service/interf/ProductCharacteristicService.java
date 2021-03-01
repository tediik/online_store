package com.jm.online_store.service.interf;

import com.jm.online_store.model.ProductCharacteristic;
import com.jm.online_store.model.dto.ProductCharacteristicDto;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductCharacteristicService {

    Long addProductCharacteristic(long productId, long characteristicId, String value);

    @Transactional
    ProductCharacteristic addProductCharacteristic(long productId, String characteristicName, String value) throws IncorrectResultSizeDataAccessException;

    List<ProductCharacteristic> addProductCharacteristic(List<ProductCharacteristicDto> list , String addedProductName);
}
