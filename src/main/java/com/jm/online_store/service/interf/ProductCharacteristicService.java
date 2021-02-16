package com.jm.online_store.service.interf;

import com.jm.online_store.model.ProductCharacteristic;
import org.springframework.transaction.annotation.Transactional;

public interface ProductCharacteristicService {

    Long addProductCharacteristic(long productId, long characteristicId, String value);

    @Transactional
    ProductCharacteristic addProductCharacteristic(long productId, String characteristicName, String value);
}
