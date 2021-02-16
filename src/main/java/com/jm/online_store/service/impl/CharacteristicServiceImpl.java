package com.jm.online_store.service.impl;

import com.jm.online_store.exception.CategoriesNotFoundException;
import com.jm.online_store.exception.CharacteristicNotFoundException;
import com.jm.online_store.exception.aatest.ExceptionConstants;
import com.jm.online_store.exception.aatest.ExceptionEnums;
import com.jm.online_store.model.Categories;
import com.jm.online_store.model.Characteristic;
import com.jm.online_store.repository.CategoriesRepository;
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
    private final CategoriesRepository categoriesRepository;

    /**
     * Метод добавления характеристики
     *
     * @param characteristic характеристика, сохраняемая в базу
     * @return id сохранённого объекта
     */
    @Override
    @Transactional
    public Characteristic saveCharacteristic(Characteristic characteristic) {
        return characteristicRepository.save(characteristic);
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
     * Метод поиска характеристи по наименованию
     *
     * @param cahracteristicName наименование искомой характеристики
     * @return Optional<Characteristic>
     */
    @Override
    public Optional<Characteristic> findCharacteristicByName(String cahracteristicName) {

        return characteristicRepository.findByCharacteristicName(cahracteristicName);
    }

    /**
     * Метод поиска характеристик по id категории
     *
     * @param categoryId id категории, по которой идет поиск харакетристик
     * @return List<Characteristic>
     */
    @Override
    public List<Characteristic> findByCategoryId(Long categoryId) {
        return categoriesService.getCategoryById(categoryId).orElseThrow(() ->
                new CategoriesNotFoundException(ExceptionEnums.CATEGORY.getText()
                        + ExceptionConstants.NOT_FOUND)).getCharacteristics();
    }

    /**
     * Метод поиска характеристик по названию категории
     *
     * @param category название категории, по которой идет поиск харакетристик
     * @return List<Characteristic>
     */
    @Override
    public List<Characteristic> findByCategoryName(String category) {

        return categoriesRepository.getCategoriesByCategory(category).orElseThrow(() ->
                new CategoriesNotFoundException(ExceptionEnums.CATEGORY.getText() +
                        ExceptionConstants.NOT_FOUND)).getCharacteristics();
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
     *
     * @param characteristic харакетристика, полученная из контроллера.
     */
    @Override
    @Transactional
    public Characteristic updateCharacteristic(Characteristic characteristic) {
        getCharacteristicById(characteristic.getId());
        return characteristicRepository.save(characteristic);
    }

    /**
     * Удаляет харакетристику по идентификатору
     *
     * @param id идентификатор.
     */
    @Override
    @Transactional
    public void deleteByID(Long id) {
        List<Categories> categoriesList = categoriesService.findAll();
        Characteristic characteristicToDelete = characteristicRepository.findById(id).orElseThrow(CharacteristicNotFoundException::new);

        for (Categories categories : categoriesList) {
            List<Characteristic> characteristicList = categories.getCharacteristics();
            characteristicList.remove(characteristicToDelete);
            categoriesService.updateCategory(categories);
        }
        characteristicRepository.deleteById(id);
    }

    /**
     * Удаляет харакетристику по идентификатору из выбранной категории
     *
     * @param id идентификатор.
     */
    @Override
    @Transactional
    public void deleteByIDInSelectedCategory(Long id, String category) {
        Categories categories = categoriesService.getCategoryByCategoryName(category).orElseThrow(CategoriesNotFoundException::new);
        Characteristic characteristicToDelete = characteristicRepository.findById(id).orElseThrow(CharacteristicNotFoundException::new);
        List<Characteristic> characteristicList = categories.getCharacteristics();

        characteristicList.remove(characteristicToDelete);
        categoriesService.updateCategory(categories);
    }

    /**
     * Ищет харакетристику по имени характеристики
     *
     * @param characteristicName имя харакетристики
     */
    @Override
    public Optional<Characteristic> findByCharacteristicName(String characteristicName) {
        return characteristicRepository.findByCharacteristicName(characteristicName);
    }


    /**
     * Ищет харакетристику по id характеристики
     * аналог метод findCharacteristicById только возвращает не Optional
     * @param id - идентификатор хар-ки
     */
    @Override
    public Characteristic getCharacteristicById(Long id) {
        return characteristicRepository.findById(id).orElseThrow(()
                -> new CharacteristicNotFoundException(ExceptionEnums.CHARACTERISTIC.getText() + ExceptionConstants.NOT_FOUND));
    }
}
