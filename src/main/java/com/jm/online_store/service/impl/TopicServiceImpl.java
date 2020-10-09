package com.jm.online_store.service.impl;

import com.jm.online_store.model.Topic;
import com.jm.online_store.repository.TopicRepository;
import com.jm.online_store.service.interf.TopicService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TopicServiceImpl implements TopicService {
    private final TopicRepository topicRepository;

    @Override
    public void addTopic(Topic topic) {
        topicRepository.save(topic);
    }

    @Override
    public List<String> getAllCategories() {
        return topicRepository.findAllCategories();
    }

    @Override
    public List<Topic> getTopicsByCategory(String category) {
        return topicRepository.findTopicByTopicCategoryEquals(category);
    }
}