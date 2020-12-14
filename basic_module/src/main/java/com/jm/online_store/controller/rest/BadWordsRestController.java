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
@RequestMapping(value = "/rest/bad-words")
public class BadWordsRestController {
    private final BadWordsService badWordsService;
    private final CommonSettingsService commonSettingsService;

    /**
     * Метод возвращает все стоп-слов, включая неактивные
     * в формте ResponseEntity<List<BadWords>> {@link ResponseEntity}
     *
     * @return
     */
    @GetMapping(value = "/all")
    public ResponseEntity<List<BadWords>> findAll() {
        return ResponseEntity.ok(badWordsService.findAllWords());
    }

    /**
     * Метод принимает id стоп-слова @PathVariable("id") Long id
     * И если найдено, возвращает стоп-слово ResponseEntity<BadWords>
     * Может выбросить исключение BadWordsNotFoundException если слово не найдено
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<BadWords> getWordById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(badWordsService.findWordById(id));
    }

    /**
     * Метод для добавление нового стоп-слова
     * Принимает @RequestBody BadWords badWords
     * Если слово пустое дает ответ EmptyBadWord. Если слово есть дает ответ duplicatedWordName
     * Иначе возвращает статуст запроса
     *
     * @param badWords
     * @return
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
     * Принимает стоп-слово @RequestBody BadWords badWords
     * Возвращает статуст запроса
     * Может выбросить исключение BadWordsNotFoundException если слово не найдено
     *
     * @param badWords
     * @return
     */
    @PutMapping("/update")
    public ResponseEntity<BadWords> updateWord(@RequestBody BadWords badWords) {
        badWordsService.saveWord(badWords);
        return ResponseEntity.ok().build();
    }

    /**
     * Метод для удаление стоп-слова
     * Принимает id слова для удаления @PathVariable Long id
     * Возвращает статус запроса
     * Может выбросить исключение BadWordsNotFoundException если слово не найдено
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Long> newsDelete(@PathVariable Long id) {
        badWordsService.deleteWord(badWordsService.findWordById(id));
        return ResponseEntity.ok().build();
    }

    /**
     * Метод возвращает список только активных стоп-слов для фильтра
     * Формат возврата ResponseEntity<List<BadWords>>
     * Может выбросить исключение BadWordsNotFoundException если слово не найдено
     *
     * @return
     */
    @GetMapping(value = "/get-active")
    public ResponseEntity<List<BadWords>> findAllActive() {
        return ResponseEntity.ok(badWordsService.findAllWordsActive());
    }

    /**
     * Метод для получение текущего статуса фильтра (true, false)
     * Возвращает ResponseEntity<CommonSettings>
     *
     * @return
     */
    @GetMapping(value = "/status")
    public ResponseEntity<CommonSettings> getSetting() {
        CommonSettings templateBody = commonSettingsService.getSettingByName("bad_words_enabled");
        return ResponseEntity.ok(templateBody);
    }

    /**
     * Метод сохраняет текущей стаутса фильтра (true, false)
     * Примает стутс в формате @RequestBody Boolean saveSetting
     * Сохраняет в CommonSettings парамаетр bad_words_enabled (true, false)
     *
     * @param saveSetting
     */
    @PostMapping(value = "/status")
    public void Setsetting(@RequestBody Boolean saveSetting) {
        if (saveSetting) {
            CommonSettings setYes = CommonSettings.builder()
                    .settingName("bad_words_enabled")
                    .textValue("true")
                    .build();
            commonSettingsService.updateTextValue(setYes);
        } else {
            CommonSettings setNo = CommonSettings.builder()
                    .settingName("bad_words_enabled")
                    .textValue("false")
                    .build();
            commonSettingsService.updateTextValue(setNo);
        }
    }

    /**
     * Метод для импорта стоп-слов в кабинете админа.
     * Принимает @RequestBody String text
     * Фозвращает статус запроса
     * Проверка слов в методе importWord
     * Формат ввода слов в input через запятую. Слово должно быть длинее 1 символа.
     * Если слово уже есть в базе, пропускает.
     *
     * @param text
     * @return
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
     * Метод для проверки комментарев (отзывав) перед публикацией
     * Принимает текст @RequestBody String checkText
     * Возвращает статус запроса, или если стоп-слова найдены List<String>
     *
     * @param checkText
     * @return
     */
    @PostMapping(value = "/check-comment")
    public ResponseEntity checkComment(@RequestBody String checkText) {
        if (checkText.equals("")) {
            return new ResponseEntity("EmptyRequest", HttpStatus.BAD_REQUEST);
        }
        CommonSettings templateBody = commonSettingsService.getSettingByName("bad_words_enabled");
        if (templateBody.getTextValue().equals("true")) {
            return new ResponseEntity("TextOK", HttpStatus.OK);
        }

        if (badWordsService.checkComment(checkText).isEmpty()) {
            return new ResponseEntity("TextOK", HttpStatus.OK);
        } else {
            return ResponseEntity.ok(badWordsService.checkComment(checkText).toString());
        }
    }

    /**
     * Exception handler method that catches all {@link BadWordsNotFoundException}
     * in current class and return ResponseEntity with not found status
     *
     * @return - {@link ResponseEntity<String>}
     */
    @ExceptionHandler(BadWordsNotFoundException.class)
    public ResponseEntity<String> badWordsNotFoundExceptionHandler() {
        return ResponseEntity.notFound().build();
    }
}