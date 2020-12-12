package com.jm.online_store.service.impl;

import com.jm.online_store.model.Characteristic;
import com.jm.online_store.repository.CharacteristicRepository;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.service.interf.CharacteristicService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CharacteristicServiceImpl implements CharacteristicService {

    private final CharacteristicRepository characteristicRepository;
    private final CategoriesService categoriesService;

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

    /**
     * Метод поиска характеристи по id
     *
     * @param id id искомой характеристики
     * @return Optional<Characteristic>
     */
    @Override
    public Optional<Characteristic> findCharacteristicById(Long id) {

        return characteristicRepository.findById(id);
    }

    /**
     * Метод поиска характеристик по id категории
     *
     * @param categoryId id категории, по которой идет поиск харакетристик
     * @return List<Characteristic>
     */
    @Override
    public List<Characteristic> findByCategoryId(Long categoryId) {

        return categoriesService.getCategoryById(categoryId).get().getCharacteristics();
    }

    /**
     * Метод получения всех характеристик
     *
     * @return List<Characteristic>
     */

    @Override
    public List<Characteristic> findAll() {
        return characteristicRepository.findAll();
    }
}
