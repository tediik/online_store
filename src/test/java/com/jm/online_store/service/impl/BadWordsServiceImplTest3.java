package com.jm.online_store.service.impl;

import com.jm.online_store.model.BadWords;
import com.jm.online_store.repository.BadWordsRepository;
import com.jm.online_store.service.interf.CommonSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

public class BadWordsServiceImplTest3 {
    @MockBean
    private BadWordsRepository badWordsRepository;
    @MockBean
    private CommonSettingsService commonSettingsService;

    @Autowired
    private BadWordsServiceImpl badWordsService;


    private List<BadWords> allActiveBW;
    private List<BadWords> ReturnBadWords;
    private String incoming;

    @BeforeEach
    void init() {
        allActiveBW = Arrays.asList(
                new BadWords("поршивка",true),
                new BadWords("евошняя",false),
                new BadWords("глазница",true),
                new BadWords("гад",true));
        incoming="lol'Ах ты ПОРШиВка!!!.ббббббр' - говно Закричал поп:'Мне не нужна евошняя Титановая Глазница...GGH5'";
        ReturnBadWords = Arrays.asList(
                new BadWords("поршивка",true),
                new BadWords("евошняя",false),
                new BadWords("глазница",true));
    }

    @Test
    void checkComment() {
//        when(badWordsRepository.getBadWordsActive()).thenReturn(Optional.ofNullable(allActiveBW));
//        when(badWordsService.findAllWordsActive()).thenReturn(Optional.ofNullable(allActiveBW));


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

