package com.jm.online_store.controller.rest;

import com.jm.online_store.enums.BadWordStatus;
import com.jm.online_store.exception.BadWordsNotFoundException;
import com.jm.online_store.model.BadWords;
import com.jm.online_store.model.CommonSettings;
import com.jm.online_store.service.interf.BadWordsService;
import com.jm.online_store.service.interf.CommonSettingsService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Рест контроллер для управления Стоп-словами в кабинете Админа
 */
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(value = "/rest/bad-words")
public class BadWordsRestController {
    BadWordsService badWordsService;
    UserService userService;
    CommonSettingsService commonSettingsService;

    /**
     * Получения всех стоп-слов
     *
     * @return
     */
    @GetMapping(value = "/all")
    public ResponseEntity<List<BadWords>> findAll() {
        return ResponseEntity.ok(badWordsService.findAllWords());
    }

    /**
     * Получения стоп-слова по id
     * Может выбросить исключение BadWordsNotFoundException если слово не найдено
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<BadWords> getWordById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(badWordsService.findWordById(id));
        } catch (BadWordsNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Добавление стоп-слова
     * Если слово пустое дает ответ EmptyBadWord. Если слово есть дает ответ duplicatedWordName
     *
     * @param badWords
     * @return
     */
    @PostMapping("/add")
    public ResponseEntity<BadWords> addWord(@RequestBody BadWords badWords) {
        String newWord = badWords.getBadword().toLowerCase();
        if (newWord.equals("")) {
            log.debug("EmptyBadWord");
            return new ResponseEntity("EmptyBadWord", HttpStatus.BAD_REQUEST);
        }

        if (badWordsService.existsBadWordByName(newWord)) {
            log.debug("Word with name: {} already exists", newWord);
            return new ResponseEntity("duplicatedWordName", HttpStatus.BAD_REQUEST);
        }

        badWordsService.saveWord(badWords);
        return ResponseEntity.ok(badWords);
    }

    /**
     * Изменение стоп слова.
     * Может выбросить исключение BadWordsNotFoundException если слово не найдено
     *
     * @param badWords
     * @return
     */
    @PutMapping("/update")
    public ResponseEntity<BadWords> updateWord(@RequestBody BadWords badWords) {
        try {
            badWordsService.saveWord(badWords);
            return ResponseEntity.ok().build();
        } catch (BadWordsNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Удаление стоп-слова
     * Может выбросить исключение BadWordsNotFoundException если слово не найдено
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Long> newsDelete(@PathVariable Long id) {
        try {
            badWordsService.deleteWord(badWordsService.findWordById(id));
            return ResponseEntity.ok().build();
        } catch (BadWordsNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Получения списка только активных стоп-слов для фильтра
     * Может выбросить исключение BadWordsNotFoundException если слово не найдено
     *
     * @return
     */
    @GetMapping(value = "/get-active")
    public ResponseEntity<List<BadWords>> findAllActive() {
        try {
            return ResponseEntity.ok(badWordsService.findAllWordsActive());
        } catch (BadWordsNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Получение текущего статуса фильтра (включен, выключен)
     *
     * @return
     */
    @GetMapping(value = "/status")
    public ResponseEntity<CommonSettings> getSetting() {
        CommonSettings templateBody = commonSettingsService.getSettingByName("bad_words_enabled");
        return ResponseEntity.ok(templateBody);
    }

    /**
     * Сохранение текущего стаутса фильтра.
     * Сохраняет в CommonSettings парамаетр bad_words_enabled
     * Значение yes включено, no выключенно
     *
     * @param text
     */
    @PostMapping(value = "/status")
    public void Setsetting(@RequestBody String text) {
        if (text.equals("yes")) {
            CommonSettings setYes = CommonSettings.builder()
                    .settingName("bad_words_enabled")
                    .textValue("yes")
                    .build();
            commonSettingsService.updateTextValue(setYes);
        }
        if (text.equals("no")) {
            CommonSettings setNo = CommonSettings.builder()
                    .settingName("bad_words_enabled")
                    .textValue("no")
                    .build();
            commonSettingsService.updateTextValue(setNo);
        }
    }

    /**
     * Импортировение стоп-слов через запятую. Слово должно быть длинее 1 символа.
     * Если слово уже есть в базе, пропускает.
     *
     * @param text
     * @return
     */
    @PostMapping(value = "/import")
    public ResponseEntity importWord(@RequestBody String text) {
        if (text.equals("")) {
            log.debug("EmptyBadWord");
            return new ResponseEntity("EmptyBadWord", HttpStatus.BAD_REQUEST);
        }

        //Очищаем от лишних проблелов
        Pattern CLEAR_PATTERN = Pattern.compile("[\\s]+");
        String noSpace = CLEAR_PATTERN.matcher(text).replaceAll(" ").trim();

        //удаляем запятую в конце
        String noCommaEnd = noSpace.replaceAll(",$", "");

        //Разбиваем на отдельные слова в массив
        String[] importText = noCommaEnd.split(", ");

        for (String textToSave : importText) {
            if (textToSave.length() < 2) continue;
            if (badWordsService.existsBadWordByName(textToSave)) continue;
            BadWords badWordsToSave = new BadWords(textToSave, BadWordStatus.ACTIVE);
            badWordsService.saveWord(badWordsToSave);
            System.out.println(textToSave);
        }

        return ResponseEntity.ok().build();
    }
}