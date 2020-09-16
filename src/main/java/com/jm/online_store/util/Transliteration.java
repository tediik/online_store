package com.jm.online_store.util;

import com.ibm.icu.text.Transliterator;
import lombok.experimental.UtilityClass;

/**
 * Вспомогательный класс для транслитерации кириллицы в латиницу
 */
@UtilityClass
public class Transliteration {

    /**
     * Метод, преобразующий входную строку символов кириллицы в латинские с помощью
     * методов класса com.ibm.icu.text.Transliterator и заменяющий пробелы на подчёркивание
     *
     * @param s строка на русском, которая должна быть преобразована
     * @return транслит латиницей
     */
    public static String сyrillicToLatin(String s){
        Transliterator toLatin = Transliterator.getInstance("Russian-Latin/BGN");
        String result = toLatin.transliterate(s);
        return result.replaceAll(" ", "_");
    }

    /**
     * Метод, преобразующий входную строку символов латиницы в кириллицу с помощью
     * методов класса com.ibm.icu.text.Transliterator и заменяющий подчёркивания на пробел
     *
     * @param s строка латиницей на русском, которая должна быть преобразована
     * @return строка на русском кириллицей
     */
    public static String latinToCyrillic(String s){
        Transliterator toCyrillic = Transliterator.getInstance("Latin-Russian/BGN");
        String result = toCyrillic.transliterate(s);
        return result.replaceAll("_", " ");
    }
}
