package com.jm.online_store.service.impl;

import com.jm.online_store.model.BadWords;
import com.jm.online_store.model.CommonSettings;
import com.jm.online_store.repository.BadWordsRepository;
import com.jm.online_store.service.interf.CommonSettingsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BadWordsServiceImplTest {
    private BadWordsRepository badWordsRepository = mock(BadWordsRepository.class);
    private CommonSettingsService commonSettingsService = mock(CommonSettingsService.class);
    private BadWordsServiceImpl badWordsService = new BadWordsServiceImpl(badWordsRepository, commonSettingsService);
    private CommonSettings templateBody = new CommonSettings(1L, "bad_words_enabled", "true", false);


    private List<BadWords> allActiveBW;
    private List<String> expectedBadWords;
    private String incoming;
    private String incoming2;


    @Test
    void importWord() {
        incoming2 = "Winter, , g, Spring, Summer, Autumn, h";
        badWordsService.importWord(incoming2);
        verify(badWordsRepository, times(4)).existsBadWordsByBadword(any(String.class));
        verify(badWordsRepository, times(4)).save(any(BadWords.class));
    }

    @Test
    void preparingWordsForImport() {
        incoming="Winter,   ,    g, Spring,,      Sum - mer,   Autumn,    765,";
        String[] expected= {"Winter", "", "g", "Spring,", "Sum - mer", "Autumn", "765"};
        try {
            Method method = BadWordsServiceImpl.class.getDeclaredMethod("preparingWordsForImport", String.class);
            method.setAccessible(true);
            assertArrayEquals(expected, (Object[]) method.invoke(badWordsService, incoming));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    void CheckComment() {
        allActiveBW = Arrays.asList(
                new BadWords("поршивка", true),
                new BadWords("евошняя", false),
                new BadWords("глазница", true),
                new BadWords("гад", true));
        incoming = "lol'Ах ты ПОРШиВка!!!.ббббббр' - Закричал поп:'Мне не нужна евошняя Титановая Глазница...GGH5'";
        expectedBadWords = Arrays.asList("поршивка", "евошняя", "глазница");
        Mockito.when(badWordsService.findAllWordsActive()).thenReturn(allActiveBW);
        Mockito.when(commonSettingsService.getSettingByName("bad_words_enabled")).thenReturn(templateBody);
        List<String> actual = badWordsService.checkComment(incoming);
        assertEquals(expectedBadWords, actual);
    }
}
