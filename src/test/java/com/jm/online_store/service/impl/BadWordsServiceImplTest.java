package com.jm.online_store.service.impl;

import com.jm.online_store.model.BadWords;
import com.jm.online_store.model.Product;
import com.jm.online_store.repository.AddressRepository;
import com.jm.online_store.repository.BadWordsRepository;
import com.jm.online_store.service.interf.CommonSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.model.Product;
import com.jm.online_store.repository.ProductRepository;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.service.interf.CommonSettingsService;
import com.jm.online_store.service.interf.EvaluationService;
import com.jm.online_store.service.interf.MailSenderService;
import com.jm.online_store.service.interf.ProductCharacteristicService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class BadWordsServiceImplTest {
    @InjectMocks
    BadWordsServiceImpl badWordsService;
    @Mock
    private BadWordsRepository badWordsRepository;
    @Mock
    private CommonSettingsService commonSettingsService;
    private List<BadWords> allActiveBWw;
    private String incoming;

    @BeforeEach
    void init() {
        allActiveBWw = Arrays.asList(
                new BadWords("поршивка",true),
                new BadWords("евошняя",false),
                new BadWords("Глазница",true),
                new BadWords("гад",true));
        incoming="lol'Ах ты ПОРШиВка!!!.ббббббр' - говно Закричал поп:'Мне не нужна евошняя Титановая Глазница...GGH5'";

    }

    @Test
    void checkComment() {
        String[] expect={"поршивка", "евошняя", "глазница"};
        List<String> expected = Arrays.asList(expect);

        String textForCheck = incoming.toLowerCase();
        System.out.println(textForCheck);
//        List<BadWords> allActiveBW = Arrays.asList(new BadWords("поршивка",true),new BadWords("евошняя",false),
//                new BadWords("Глазница",true),new BadWords("гад",true));
//        when(badWordsRepository.getBadWordsActive()).thenReturn(Optional.ofNullable(allActiveBWw));
//             Optional<BadWords> optionalProduct = badWordsService.checkComment(incoming);
//             assertNotNull(optionalProduct);
//             verify(badWordsRepository, times(1)).findById(product.getId());

        List<BadWords> allActiveBW = badWordsRepository.getBadWordsActive();
        List<String> foundWords = new ArrayList<>();
        for (BadWords wordCheck : allActiveBW) {
            String findMe = wordCheck.getBadword().toLowerCase();
            if (textForCheck.matches(".*?\\b" + findMe + "\\b.*?")) {
                foundWords.add(findMe);
            }
        }
//        String[] actual ={"2","3"};
        System.out.println(foundWords);
        assertLinesMatch(expected,foundWords);

//        List<String> strings = badWordsService.checkComment(incoming);
//        System.out.println(strings);
//
//        assertArrayEquals(expected,strings.toArray());
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



    @Test
    void preparingWordsForImport() {
        String incoming="df, gf, dfgdfg,  , fdg,  f, dSF GGH5    sdf  dsfs, sd,.2sd. dsf .sdf f.sd,233.,sdf,  sd,f,sdf,.sdfsd,f,.sdf  df dsfsdfdf,      df,.,,234";
        String[] actual={"2","3"};
        String[] expected={"2","3"};
        assertArrayEquals(expected,actual);
//        badWordsService.preparingWordsForImport();

        Pattern CLEAR_PATTERN = Pattern.compile("[\\s]+");
        String noSpace = CLEAR_PATTERN.matcher(incoming).replaceAll(" ").trim();
//        System.out.println(noSpace+"__________________________");
        String noCommaEnd = noSpace.replaceAll(",$", "");
        String[] importText = noCommaEnd.split(", ");
        for(String a : importText){
        System.out.println(a);}

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


//    @Test
//    void importWord() {
//    }
//

}