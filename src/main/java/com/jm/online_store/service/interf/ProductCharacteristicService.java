package com.jm.online_store.service.interf;

import org.springframework.transaction.annotation.Transactional;

public interface ProductCharacteristicService {

    Long addProductCharacteristic(long productId, long characteristicId, String value);

    @Transactional
    Long addProductCharacteristic(long productId, String characteristicName, String value);
}
