package com.jm.online_store.service.interf;

import com.jm.online_store.model.Characteristic;

import java.util.List;
import java.util.Optional;

public interface CharacteristicService {

    Long saveCharacteristic(Characteristic characteristic);

    Optional<Characteristic> findCharacteristicById(Long id);

    List<Characteristic> findByCategoryId(Long categoryId);

    List<Characteristic> findByCategoryName(String category);

    List<Characteristic> findAll();

    void updateCharacteristic(Characteristic characteristic);

    void deleteByID(Long id);
}
