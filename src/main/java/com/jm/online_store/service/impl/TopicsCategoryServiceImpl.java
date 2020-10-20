package com.jm.online_store.service.impl;

import com.jm.online_store.model.TopicsCategory;
import com.jm.online_store.repository.TopicsCategoryRepository;
import com.jm.online_store.service.interf.TopicsCategoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class TopicsCategoryServiceImpl implements TopicsCategoryService {

    private final TopicsCategoryRepository topicsCategoryRepository;

    @Override
    @Transactional
    public void creat(TopicsCategory topicsCategory) {
        topicsCategoryRepository.saveAndFlush(topicsCategory);
    }

    @Override
    public List<TopicsCategory> findAll() {
        return topicsCategoryRepository.findAll();
    }

    @Override
    public List<TopicsCategory> findAllByActualIsTrue() {
        return topicsCategoryRepository.findAllByActualIsTrue();
    }

    @Override
    public List<TopicsCategory> findAllByActualIsFalse() {
        return topicsCategoryRepository.findAllByActualIsFalse();
    }

    @Override
    public TopicsCategory findById(Long id) {
        return topicsCategoryRepository.findById(id).get();
    }

    @Override
    public TopicsCategory findByCategoryName(String categoryName) {
        return topicsCategoryRepository.findByCategoryName(categoryName);
    }

    @Override
    public boolean existsById(long id) {
        return topicsCategoryRepository.existsById(id);
    }

    @Override
    public boolean existsByCategoryName(String categoryName) {
        return topicsCategoryRepository.existsByCategoryName(categoryName);
    }

    @Override
    @Transactional
    public void update(TopicsCategory topicsCategory) {
        topicsCategoryRepository.saveAndFlush(topicsCategory);
    }

    @Override
    @Transactional
    public void archive(TopicsCategory topicsCategory) {
        topicsCategory.setActual(false);
        topicsCategoryRepository.saveAndFlush(topicsCategory);
    }

    @Override
    @Transactional
    public void unarchive(TopicsCategory topicsCategory) {
        topicsCategory.setActual(true);
        topicsCategoryRepository.saveAndFlush(topicsCategory);
    }
}
