package com.jm.online_store.controller.rest;

import com.jm.online_store.exception.BadWordsNotFoundException;
import com.jm.online_store.model.BadWords;
import com.jm.online_store.model.CommonSettings;
import com.jm.online_store.service.interf.BadWordsService;
import com.jm.online_store.service.interf.CommonSettingsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Рест контроллер для управления Стоп-словами в кабинете Админа
 */
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(value = "/api/rest/bad-words")
public class BadWordsRestController {
    private final BadWordsService badWordsService;
    private final CommonSettingsService commonSettingsService;

    /**
     * Метод возвращает все стоп-слова, включая неактивные
     * @return ResponseEntity<List<BadWords>> список стоп-слов
     */
    @GetMapping(value = "/all")
    public ResponseEntity<List<BadWords>> findAll() {
        return ResponseEntity.ok(badWordsService.findAllWords());
    }

    /**
     * Метод принимает id стоп-слова
     * Если стоп-слово найдено, возвращает стоп-слово BadWords
     * Может выбросить исключение BadWordsNotFoundException, если слово не найдено
     * @param id идентификатор стоп-слова
     * @return ResponseEntity<BadWords> стоп-слово
     */
    @GetMapping("/{id}")
    public ResponseEntity<BadWords> getWordById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(badWordsService.findWordById(id));
    }

    /**
     * Метод для добавления нового стоп-слова
     * Если слово пустое дает ответ EmptyBadWord. Если слово есть, возвращает ответ duplicatedWordName, 
     * иначе возвращает статус запроса
     * @param badWords {@link BadWords} сущность BadWords стоп-слово
     * @return ResponseEntity<BadWords> 
     */
    @PostMapping("/add")
    public ResponseEntity<BadWords> addWord(@RequestBody BadWords badWords) {
        String newWord = badWords.getBadword().toLowerCase();
        if (newWord.equals("")) {
            return new ResponseEntity("EmptyBadWord", HttpStatus.BAD_REQUEST);
        }

        if (badWordsService.existsBadWordByName(newWord)) {
            return new ResponseEntity("duplicatedWordName", HttpStatus.BAD_REQUEST);
        }

        badWordsService.saveWord(badWords);
        return ResponseEntity.ok(badWords);
    }

    /**
     * Метод для изменение стоп слова.
     * Может выбросить исключение BadWordsNotFoundException если слово не найдено
     * @param badWords {@link BadWords} BadWords стоп-слово
     * @return ResponseEntity<BadWords> стоп-слово
     */
    @PutMapping("/update")
    public ResponseEntity<BadWords> updateWord(@RequestBody BadWords badWords) {
        badWordsService.saveWord(badWords);
        return ResponseEntity.ok().build();
    }

    /**
     * Метод удаления стоп-слова
     * Может выбросить исключение BadWordsNotFoundException если слово не найдено
     * @param id {@link Long} id BadWords стоп-слова для удаления
     * @return ResponseEntity<Long>
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Long> newsDelete(@PathVariable Long id) {
        badWordsService.deleteWord(badWordsService.findWordById(id));
        return ResponseEntity.ok().build();
    }

    /**
     * Метод возвращает список только активных стоп-слов для фильтра
     * Может выбросить исключение BadWordsNotFoundException если слово не найдено
     * @return ResponseEntity<List<BadWords>> список активных стоп-слов для фильтра
     */
    @GetMapping(value = "/get-active")
    public ResponseEntity<List<BadWords>> findAllActive() {
        return ResponseEntity.ok(badWordsService.findAllWordsActive());
    }

    /**
     * Метод для получения текущего статуса фильтра (true, false)
     * @return ResponseEntity<CommonSettings>
     */
    @GetMapping(value = "/status")
    public ResponseEntity<CommonSettings> getSetting() {
        CommonSettings templateBody = commonSettingsService.getSettingByName("bad_words_enabled");
        return ResponseEntity.ok(templateBody);
    }

    /**
     * Метод сохраняет текущий статус фильтра (true, false)
     * Сохраняет в CommonSettings параметр bad_words_enabled (true, false)
     * @param isEnabled {@link Boolean} включен или нет фильтр
     */
    @PostMapping(value = "/status")
    public void setSetting(@RequestBody Boolean isEnabled) {
        String status = "true";
        if (!isEnabled) {
            status = "false";
        }
        CommonSettings setStatus = CommonSettings.builder()
                .settingName("bad_words_enabled")
                .textValue(status)
                .build();
        commonSettingsService.updateTextValue(setStatus);
    }

    /**
     * Метод для импорта стоп-слов в кабинете админа из переданного текста.
     * Проверка слов в методе importWord()
     * Формат ввода слов - через запятую. Слово должно быть длинее 1 символа.
     * Если слово уже есть в базе, пропускает.
     *
     * @param text {@link String} текст со стоп-словами
     * @return ResponseEntity статус ответа
     */
    @PostMapping(value = "/import")
    public ResponseEntity importWord(@RequestBody String text) {
        if (text.equals("")) {
            return new ResponseEntity("EmptyBadWord", HttpStatus.BAD_REQUEST);
        }
        badWordsService.importWord(text);

        return ResponseEntity.ok().build();
    }

    /**
     * Exception handler method that catches all {@link BadWordsNotFoundException}
     * in current class and return ResponseEntity with not found status
     * @return - {@link ResponseEntity<String>}
     */
    @ExceptionHandler(BadWordsNotFoundException.class)
    public ResponseEntity<String> badWordsNotFoundExceptionHandler() {
        return ResponseEntity.notFound().build();
    }
}
