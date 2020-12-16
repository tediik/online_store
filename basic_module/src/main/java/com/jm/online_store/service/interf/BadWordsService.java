package com.jm.online_store.service.interf;

import com.jm.online_store.model.BadWords;

import java.util.List;

public interface BadWordsService {
    BadWords findWordById(Long id);

    void saveWord(BadWords badWords);

    void updateWord(BadWords badWords);

    void deleteWord(BadWords badWords);

    List<BadWords> findAllWords();

    boolean existsBadWordByName(String wordName);

    List<BadWords> findAllWordsActive();

    void importWord(String words);

    List<String> checkComment(String checkText);
}
