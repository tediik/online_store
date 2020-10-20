package com.jm.online_store.service.interf;

import com.jm.online_store.model.Topic;

import java.util.List;

public interface TopicService {

    void creat(Topic topic);

    List<Topic> getTopicsByCategoryId(long id);

    boolean existsByTopicName(String topicName);

    boolean existsById(long id);

    Topic findByTopicName(String topicName);

    Topic findById(long id);

    Topic update(Topic topic);


}
