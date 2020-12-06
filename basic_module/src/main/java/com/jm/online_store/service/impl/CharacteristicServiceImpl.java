package com.jm.online_store.service.impl;

import com.jm.online_store.model.Characteristic;
import com.jm.online_store.repository.CharacteristicRepository;
import com.jm.online_store.service.interf.CharacteristicService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CharacteristicServiceImpl implements CharacteristicService {

    private final CharacteristicRepository characteristicRepository;

    /**
     * Метод добавления характеристики
     *
     * @param characteristic характеристика, сохраняемая в базу
     * @return id сохранённого объекта
     */
    @Override
    public Long addCharacteristic(Characteristic characteristic) {

        return characteristicRepository.save(characteristic).getId();
    }

    @Override
    public Optional<Characteristic> findCharacteristicById(Long id) {
        return characteristicRepository.findById(id);
    }
}
