package com.jm.online_store.service.impl;

import com.jm.online_store.exception.BadWordsNotFoundException;
import com.jm.online_store.model.BadWords;
import com.jm.online_store.model.CommonSettings;
import com.jm.online_store.repository.BadWordsRepository;
import com.jm.online_store.service.interf.BadWordsService;
import com.jm.online_store.service.interf.CommonSettingsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Класс для работы со стоп-словами, которые нужно блокировать в комментариях.
 */
@Service
@AllArgsConstructor
public class BadWordsServiceImpl implements BadWordsService {
    private final BadWordsRepository badWordsRepository;
    private final CommonSettingsService commonSettingsService;

    @Override
    public BadWords findWordById(Long id) {
        return badWordsRepository.findById(id).orElseThrow(BadWordsNotFoundException::new);
    }

    @Override
    public void saveWord(BadWords badWords) {
        badWordsRepository.save(badWords);
    }

    @Override
    public void updateWord(BadWords badWords) {
        badWordsRepository.save(badWords);
    }

    @Override
    public void deleteWord(BadWords badWords) {
        badWordsRepository.delete(badWords);
    }

    @Override
    public List<BadWords> findAllWords() {
        return badWordsRepository.findAll();
    }

    @Override
    public boolean existsBadWordByName(String wordName) {
        return badWordsRepository.existsBadWordsByBadword(wordName);
    }

    @Override
    public List<BadWords> findAllWordsActive() {
        return badWordsRepository.getBadWordsActive();
    }

    /**
     * Метод принимает текст, отдает на очистку от всего лишнего в preparingWordsForImport
     * Формат ввода слов в input через запятую. Слово должно быть длинее 1 символа.
     * @param words {@link String} текст для импорта стоп-слов
     */
    @Override
    public void importWord(String words) {
        String[] importText = preparingWordsForImport(words);

        for (String textToSave : importText) {
            if (textToSave.length() < 2) continue;
            if (existsBadWordByName(textToSave)) continue;
            BadWords badWordsToSave = new BadWords(textToSave, true);
            saveWord(badWordsToSave);
        }
    }

    /**
     * Метод подготовки стоп-слов для импорта.
     * Удаляет лишние пробелы, запятые
     * @param text {@link String} текст для обработки
     * @return String[] массив стоп-слов
     */
    private String[] preparingWordsForImport(String text) {
        //Очищаем от лишних пробелов
        Pattern CLEAR_PATTERN = Pattern.compile("[\\s]+");
        String noSpace = CLEAR_PATTERN.matcher(text).replaceAll(" ").trim();

        //удаляем запятую в конце
        String noCommaEnd = noSpace.replaceAll(",$", "");

        //Разбиваем на отдельные слова в массив
        String[] importText = noCommaEnd.split(", ");

        return importText;
    }

    /**
     * Метод принимает текст, и ищет в нем стоп-слова.
     * @param checkText {@link String} текст со стоп-словами
     * @return List<String> Список стоп-слов
     */
    @Override
    public List<String> checkComment(String checkText) {
        String textForCheck = checkText.toLowerCase();

        List<BadWords> allActiveBW = findAllWordsActive();
//         if(findAllWordsActive()==null){
//            allActiveBW = Arrays.asList(new BadWords("lol",true));
//        }else {
//            allActiveBW = findAllWordsActive();
//        }
        List<String> foundWords = new ArrayList<>();
        for (BadWords wordCheck : allActiveBW) {
            String findMe = wordCheck.getBadword().toLowerCase();
            if (textForCheck.matches(".*?\\b" + findMe + "\\b.*?")) {
                foundWords.add(findMe);
            }
        }

        return foundWords;
    }

    /**
     * Метод проверяет, включен или нет фильтр стоп-слов
     * False выключен, True включен
     * Значения хранятся в параметре bad_words_enabled
     *
     * @return boolean если фильтр включен, возвращает true
     */
    public boolean checkEnabledCheckText() {
        CommonSettings templateBody = commonSettingsService.getSettingByName("bad_words_enabled");
        if (templateBody.getTextValue().equals("false")) return false;
        return true;
    }
}
