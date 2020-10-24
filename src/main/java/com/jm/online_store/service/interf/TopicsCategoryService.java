package com.jm.online_store.service.interf;

import com.jm.online_store.model.TopicsCategory;

import java.util.List;

public interface TopicsCategoryService {

    List<TopicsCategory> findAll();

    List<TopicsCategory> findAllByActualIsTrue();

    TopicsCategory findById(Long id);

    TopicsCategory findByCategoryName(String categoryName);

    boolean existsById(long id);

    boolean existsByCategoryName(String categoryName);

    TopicsCategory create(TopicsCategory topicsCategory);

    TopicsCategory update(TopicsCategory topicsCategory);

    TopicsCategory archive(TopicsCategory topicsCategory);

    TopicsCategory unarchive(TopicsCategory topicsCategory);
}
