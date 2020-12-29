package com.jm.online_store.service.impl;

import com.jm.online_store.model.BadWords;
import com.jm.online_store.repository.BadWordsRepository;
import com.jm.online_store.service.interf.CommonSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
public class BadWordsServiceImplTest2 {

    private BadWordsRepository badWordsRepository;
    private CommonSettingsService commonSettingsService;

    private BadWordsServiceImpl badWordsService;

    public BadWordsServiceImplTest2() {
        this.badWordsService = new BadWordsServiceImpl(badWordsRepository, commonSettingsService);
    }

    private List<BadWords> allActiveBW;
    private String incoming;

    @BeforeEach
    void init() {
        allActiveBW = Arrays.asList(
                new BadWords("поршивка",true),
                new BadWords("евошняя",false),
                new BadWords("Глазница",true),
                new BadWords("гад",true));
        incoming="lol'Ах ты ПОРШиВка!!!.ббббббр' - говно Закричал поп:'Мне не нужна евошняя Титановая Глазница...GGH5'";

    }

    @Test
    void checkComment() {
        given(badWordsRepository.getBadWordsActive()).willReturn(allActiveBW);
        badWordsService.checkComment(incoming);

//        boolean ew= badWordsService.checkComment(incoming);

    }
    /**
     * Метод принимает текст, и ищет в нем стоп-слова.
     * @param checkText {@link String} текст со стоп-словами
     * @return List<String> Список стоп-слов
     */
    public List<String> checkComment(String checkText) {
        String textForCheck = checkText.toLowerCase();

        List<BadWords> allActiveBW = badWordsRepository.getBadWordsActive();
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
