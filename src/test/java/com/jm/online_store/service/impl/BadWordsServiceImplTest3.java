package com.jm.online_store.service.impl;

import com.jm.online_store.model.BadWords;
import com.jm.online_store.repository.BadWordsRepository;
import com.jm.online_store.service.interf.CommonSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
//
//@RunWith(SpringRunner.class)

@SpringBootTest
public class BadWordsServiceImplTest3 {
    @MockBean
    private BadWordsRepository badWordsRepository;
    @MockBean
    private CommonSettingsService commonSettingsService;

    @Autowired
    private BadWordsServiceImpl badWordsService;

    private List<BadWords> allActiveBW;
    private List<String> expectedBadWords;
    private String incoming;
    private String incoming2;
    private BadWords df;



    @BeforeEach
    void init() {

        df = new BadWords("rfrf",true);

    }


//
    @Test
    void preparingWordsForImport() { }

    public String[] preparingWordsForImport(String text) {
//    private String[] preparingWordsForImport(String text) {
        //Очищаем от лишних пробелов
        Pattern CLEAR_PATTERN = Pattern.compile("[\\s]+");
        String noSpace = CLEAR_PATTERN.matcher(text).replaceAll(" ").trim();

        //удаляем запятую в конце
        String noCommaEnd = noSpace.replaceAll(",$", "");

        //Разбиваем на отдельные слова в массив
        String[] importText = noCommaEnd.split(", ");

        return importText;
    }
    @Test
    void importWord() {
        incoming2="Winter, , g, Spring, Summer, Autumn, h";
        incoming="Winter, , g, Spring, Summer, Autumn, h";
        String[] as= {"Winter", "Spring", "Summer", "Autumn"};
//        Mockito.when(badWordsService.preparingWordsForImport(incoming)).thenReturn(as);
//        doReturn(as).when((badWordsService).preparingWordsForImport(incoming));
//        Mockito.when(badWordsService.findAllWordsActive()).thenReturn(allActiveBW);
       badWordsService.importWord(incoming2);
//        verify(badWordsService, times(1)).preparingWordsForImport(any());
        verify(badWordsRepository, times(4)).existsBadWordsByBadword(any(String.class));
        verify(badWordsRepository, times(4)).save(any(BadWords.class));
    }

    /**
     * Метод принимает текст, отдает на очистку от всего лишнего в preparingWordsForImport
     * Формат ввода слов в input через запятую. Слово должно быть длинее 1 символа.
     * @param  {@link String} текст для импорта стоп-слов
     */
    @Test
    void CheckComment() {
//        when(badWordsService.checkComment(incoming)).thenReturn(expectedBadWords);
//        doReturn(expectedBadWords).when((badWordsService).checkComment(incoming));
        allActiveBW = Arrays.asList(
                new BadWords("поршивка",true),
                new BadWords("евошняя",false),
                new BadWords("глазница",true),
                new BadWords("гад",true));
        incoming="lol'Ах ты ПОРШиВка!!!.ббббббр' - говно Закричал поп:'Мне не нужна евошняя Титановая Глазница...GGH5'";

        expectedBadWords = Arrays.asList("поршивка", "евошняя", "глазница");
//        Mockito.when(badWordsRepository.getBadWordsActive()).thenReturn(allActiveBW);
        Mockito.when(badWordsService.findAllWordsActive()).thenReturn(allActiveBW);
        List<String> actual = badWordsService.checkComment(incoming);
        assertEquals(expectedBadWords, actual);
    }

}
