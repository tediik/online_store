package com.jm.online_store.util;

import com.ibm.icu.text.Transliterator;

public class Transliteration {

    public static String сyrillicToLatin(String s){
        Transliterator toLatin = Transliterator.getInstance("Russian-Latin/BGN");
        String result = toLatin.transliterate(s);
        return result;
    }
}
