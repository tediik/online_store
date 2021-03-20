package com.jm.online_store.controller.rest;

import com.jm.online_store.enums.ResponseOperation;
import com.jm.online_store.exception.BadWordsNotFoundException;
import com.jm.online_store.model.BadWords;
import com.jm.online_store.model.CommonSettings;
import com.jm.online_store.model.dto.BadWordsDto;
import com.jm.online_store.model.dto.CommonSettingsDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.BadWordsService;
import com.jm.online_store.service.interf.CommonSettingsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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

import java.lang.reflect.Type;
import java.util.List;

/**
 * Рест контроллер для управления Стоп-словами в кабинете Админа
 */
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(value = "/api/bad-words")
public class BadWordsRestController {
    private final BadWordsService badWordsService;
    private final CommonSettingsService commonSettingsService;
    private final ModelMapper modelMapper;
    private final Type listType = new TypeToken<List<BadWordsDto>>() {}.getType();

    /**
     * Метод возвращает все стоп-слова, включая неактивные
     * @return ResponseEntity<List<BadWords>> список стоп-слов
     */
    @GetMapping(value = "/all")
    @ApiOperation(value = "return a list of stop words including inactive",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<List<BadWordsDto>>> findAll() {
        List<BadWordsDto> badWordsDto = modelMapper.map(badWordsService.findAllWords(), listType);
        return new ResponseEntity<>(new ResponseDto<>(true, badWordsDto), HttpStatus.OK);
    }

    /**
     * Метод принимает id стоп-слова
     * Если стоп-слово найдено, возвращает стоп-слово BadWords
     * Может выбросить исключение BadWordsNotFoundException, если слово не найдено
     * @param id идентификатор стоп-слова
     * @return ResponseEntity<BadWords> стоп-слово
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "return a stop word by id",
            authorizations = {@Authorization(value = "jwtToken")})
    public ResponseEntity<ResponseDto<BadWordsDto>> getWordById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(new ResponseDto<>(true,
                modelMapper.map(badWordsService.findWordById(id), BadWordsDto.class)), HttpStatus.OK);
    }

    /**
     * Метод для добавления нового стоп-слова
     * Если слово пустое дает ответ EmptyBadWord. Если слово есть, возвращает ответ duplicatedWordName, 
     * иначе возвращает статус запроса
     * @param badWords {@link BadWords} сущность BadWords стоп-слово
     * @return ResponseEntity<BadWords> 
     */
    @PostMapping("/add")
    @ApiOperation(value = "add new stop word",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<BadWordsDto>> addWord(@RequestBody BadWords badWords) {
        String newWord = badWords.getBadword().toLowerCase();
        if (newWord.equals("")) {
            return new ResponseEntity<>(new ResponseDto<>(false, "Empty bad word"), HttpStatus.BAD_REQUEST);
        }

        if (badWordsService.existsBadWordByName(newWord)) {
            return new ResponseEntity<>(new ResponseDto<>(false, "Duplicated word name"), HttpStatus.BAD_REQUEST);
        }
        badWordsService.saveWord(badWords);
        return new ResponseEntity<>(new ResponseDto<>(true, modelMapper.map(badWords, BadWordsDto.class)), HttpStatus.OK);
    }

    /**
     * Метод для изменение стоп слова.
     * Может выбросить исключение BadWordsNotFoundException если слово не найдено
     * @param badWords {@link BadWords} BadWords стоп-слово
     * @return ResponseEntity<BadWords> стоп-слово
     */
    @PutMapping("/update")
    @ApiOperation(value = "update stop word",
            authorizations = {@Authorization(value = "jwtToken")})
    public ResponseEntity<ResponseDto<BadWordsDto>> updateWord(@RequestBody BadWords badWords) {
        badWordsService.saveWord(badWords);
        return new ResponseEntity<>(new ResponseDto<>(true, modelMapper.map(badWords, BadWordsDto.class)), HttpStatus.OK);
    }

    /**
     * Метод удаления стоп-слова
     * Может выбросить исключение BadWordsNotFoundException если слово не найдено
     * @param id {@link Long} id BadWords стоп-слова для удаления
     * @return ResponseEntity<ResponseDto<String>>
     */
    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "delete stop word by id",
            authorizations = {@Authorization(value = "jwtToken")})
    public ResponseEntity<ResponseDto<String>> newsDelete(@PathVariable Long id) {
            badWordsService.deleteWord(badWordsService.findWordById(id));
            return new ResponseEntity<>(new ResponseDto<>(true, "Bad word successful deleted", ResponseOperation.NO_ERROR.getMessage()), HttpStatus.OK);
    }

    /**
     * Метод возвращает список только активных стоп-слов для фильтра
     * Может выбросить исключение BadWordsNotFoundException если слово не найдено
     * @return ResponseEntity<List<BadWords>> список активных стоп-слов для фильтра
     */
    @GetMapping(value = "/get-active")
    @ApiOperation(value = "return list of all active stop words",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<List<BadWordsDto>>> findAllActive() {
        List<BadWordsDto> badWordsDto = modelMapper.map(badWordsService.findAllWordsActive(), listType);
        return new ResponseEntity<>(new ResponseDto<>(true, badWordsDto), HttpStatus.OK);
    }

    /**
     * Метод для получения текущего статуса фильтра (true, false)
     * @return ResponseEntity<ResponseDto<CommonSettingsDto>>
     */
    @GetMapping(value = "/status")
    @ApiOperation(value = "return current status of filter (true , false)",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<CommonSettingsDto>> getSetting() {
        CommonSettings templateBody = commonSettingsService.getSettingByName("bad_words_enabled");
        return new ResponseEntity<>(new ResponseDto<>(true, modelMapper.map(templateBody, CommonSettingsDto.class)), HttpStatus.OK);
    }

    /**
     * Метод сохраняет текущий статус фильтра (true, false)
     * Сохраняет в CommonSettings параметр bad_words_enabled (true, false)
     * @param isEnabled {@link Boolean} включен или нет фильтр
     */
    @PostMapping(value = "/status")
    @ApiOperation(value = "save current status of filter (true, false)",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<CommonSettingsDto>> setSetting(@RequestBody Boolean isEnabled) {
        String status = "true";
        if (!isEnabled) {
            status = "false";
        }
        CommonSettings setStatus = CommonSettings.builder()
                .settingName("bad_words_enabled")
                .textValue(status)
                .build();
        commonSettingsService.updateTextValue(setStatus);
        return new ResponseEntity<>(new ResponseDto<>(true, modelMapper.map(setStatus, CommonSettingsDto.class)), HttpStatus.OK);
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
    @ApiOperation(value = "method for import stop words",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<String>> importWord(@RequestBody String text) {
        if (text.equals("")) {
            return new ResponseEntity<>(new ResponseDto<>(false, "EmptyBadWord"), HttpStatus.BAD_REQUEST);
        }
        badWordsService.importWord(text);
        return new ResponseEntity<>(new ResponseDto<>(true, text, ResponseOperation.NO_ERROR.getMessage()), HttpStatus.OK);
    }

    /**
     * Exception handler method that catches all {@link BadWordsNotFoundException}
     * in current class and return ResponseEntity with not found status
     * @return - {@link ResponseEntity<String>}
     */
    @ExceptionHandler(BadWordsNotFoundException.class)
    public ResponseEntity<ResponseDto<String>> badWordsNotFoundExceptionHandler() {
        return new ResponseEntity<>(new ResponseDto<>(false, "Bad words not found"), HttpStatus.NOT_FOUND);
    }
}
