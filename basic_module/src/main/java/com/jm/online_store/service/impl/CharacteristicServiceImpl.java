package com.jm.online_store.service.impl;

import com.jm.online_store.model.Categories;
import com.jm.online_store.model.Characteristic;
import com.jm.online_store.repository.CharacteristicRepository;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.service.interf.CharacteristicService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
@Transactional(readOnly = true)
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
    @Transactional
    public Long saveCharacteristic(Characteristic characteristic) {

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

    /**
     * Обновление характеристики.
     * @param characteristic пользователь, полученный из контроллера.
     */
    @Override
    @Transactional
    public void updateCharacteristic(Characteristic characteristic) {
        characteristicRepository.save(characteristic);
    }

    /**
     * Удаляет харакетристику по идентификатору
     * @param id идентификатор.
     */
    @Override
    @Transactional
    public void deleteByID(Long id) {
        List<Categories> categoriesList = categoriesService.findAll();
        Characteristic characteristicToDelete = characteristicRepository.findById(id).get();

        for (Categories categories : categoriesList) {
            List<Characteristic> characteristicList = categories.getCharacteristics();
            characteristicList.remove(characteristicToDelete);
            categoriesService.updateCategory(categories);
        }
        characteristicRepository.deleteById(id);
    }

}
