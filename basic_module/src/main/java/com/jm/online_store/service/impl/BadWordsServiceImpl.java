package com.jm.online_store.service.impl;

import com.jm.online_store.enums.BadWordStatus;
import com.jm.online_store.exception.BadWordsNotFoundException;
import com.jm.online_store.model.BadWords;
import com.jm.online_store.model.dto.BadWordsDto;
import com.jm.online_store.repository.BadWordsRepository;
import com.jm.online_store.service.interf.BadWordsService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public BadWordsDto findWordByIdDto(Long id) {
        return convertBadWordsToBadWordsDTO(badWordsRepository.findById(id).get());
    }

    @Override
    public boolean existsBadWordByName(String wordName) {
        return badWordsRepository.existsBadWordsByBadword(wordName);
    }

    @Override
    public List<BadWords> findAllWordsActive() {
        return badWordsRepository.findAllByStatusEquals(BadWordStatus.ACTIVE);
    }

    private BadWordsDto convertBadWordsToBadWordsDTO(BadWords badWords) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(badWords, BadWordsDto.class);
    }

}
