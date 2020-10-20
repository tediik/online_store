package com.jm.online_store.service.interf;

import com.jm.online_store.model.TopicsCategory;

import java.util.List;

public interface TopicsCategoryService {

    List<TopicsCategory> findAll();

    List<TopicsCategory> findAllByActualIsTrue();

    List<TopicsCategory> findAllByActualIsFalse();

    TopicsCategory findById(Long id);

    TopicsCategory findByCategoryName(String categoryName);

    boolean existsById(long id);

    boolean existsByCategoryName(String categoryName);

    void creat(TopicsCategory topicsCategory);

    void update(TopicsCategory topicsCategory);

    void archive(TopicsCategory topicsCategory);

    void unarchive(TopicsCategory topicsCategory);
}
