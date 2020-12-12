package com.jm.online_store.service.interf;

import com.jm.online_store.model.Characteristic;

import java.util.List;
import java.util.Optional;

public interface CharacteristicService {

    Long addCharacteristic(Characteristic characteristic);

    Optional<Characteristic> findCharacteristicById(Long id);

    List<Characteristic> findByCategoryId(Long categoryId);

    List<Characteristic> findAll();
}
