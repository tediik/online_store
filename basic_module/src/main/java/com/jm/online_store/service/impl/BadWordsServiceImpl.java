package com.jm.online_store.service.impl;

import com.jm.online_store.exception.BadWordsNotFoundException;
import com.jm.online_store.model.BadWords;
import com.jm.online_store.repository.BadWordsRepository;
import com.jm.online_store.service.interf.BadWordsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class BadWordsServiceImpl implements BadWordsService {
    private final BadWordsRepository badWordsRepository;

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
     * Удаляет лишние проблемы, запятые
     * Принимает текст для обработки String words
     * Возвращает массив результатов обработки String[]
     *
     * @param words
     * @return
     */
    private String[] preparingWordsForImport(String words) {
        //Очищаем от лишних проблелов
        Pattern CLEAR_PATTERN = Pattern.compile("[\\s]+");
        String noSpace = CLEAR_PATTERN.matcher(words).replaceAll(" ").trim();

        //удаляем запятую в конце
        String noCommaEnd = noSpace.replaceAll(",$", "");

        //Разбиваем на отдельные слова в массив
        String[] importText = noCommaEnd.split(", ");

        return importText;
    }

    @Override
    public List<String> checkComment(String checkText) {
        String textForCheck = checkText.toLowerCase();

        List<BadWords> allActiveBW = findAllWordsActive();
        List<String> foundWords = new ArrayList<>();
        for (BadWords wordCheck : allActiveBW) {
            String findMe = wordCheck.getBadword().toLowerCase();
            if (textForCheck.matches(".*?\\b" + findMe + "\\b.*?")) {
                foundWords.add(findMe);
            }
        }

        return foundWords;
    }
}
