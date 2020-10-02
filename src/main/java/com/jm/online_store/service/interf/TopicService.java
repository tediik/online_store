package com.jm.online_store.service.interf;

import com.jm.online_store.model.Topic;

import java.util.List;

public interface TopicService {
    void addTopic(Topic topic);

    List<String> getAllCategories();

    List<String> getTopicsByCategory(String category);
}
