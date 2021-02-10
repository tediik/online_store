package com.jm.online_store.service.impl;

import com.jm.online_store.exception.topicService.TopicExceptionConstants;
import com.jm.online_store.exception.topicService.TopicServiceException;
import com.jm.online_store.model.Topic;
import com.jm.online_store.repository.TopicRepository;
import com.jm.online_store.service.interf.TopicService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class TopicServiceImpl implements TopicService {
    private final TopicRepository topicRepository;

    @Override
    @Transactional
    public Topic create(Topic topic) {
        if (existsByTopicName(topic.getTopicName())) {
            throw new TopicServiceException(TopicExceptionConstants.ALREADY_EXISTS_TOPIC);
        }
        return topicRepository.saveAndFlush(topic);
    }

    @Override
    public List<Topic> getTopicsByCategoryId(long id) {
        return topicRepository.findTopicByTopicsCategoryId(id);
    }

    @Override
    public boolean existsByTopicName(String topicName) {
        return topicRepository.existsByTopicName(topicName);
    }

    @Override
    public boolean existsById(long id) {
        return topicRepository.existsById(id);
    }

    @Override
    public Topic findByTopicName(String topicName) {
        return topicRepository.findByTopicName(topicName);
    }

    @Override
    public Topic findById(long id) {
        Optional<Topic> optTopic = topicRepository.findById(id);
        if (optTopic.isEmpty()) {
            throw new TopicServiceException(TopicExceptionConstants.NOT_FOUND_TOPIC);
        }
        return optTopic.get();
    }

    @Override
    @Transactional
    public Topic update(Topic topic) {
        if (!existsById(topic.getId())) {
            throw new TopicServiceException(TopicExceptionConstants.NOT_FOUND_TOPIC);
        }
        return topicRepository.saveAndFlush(topic);
    }
}
