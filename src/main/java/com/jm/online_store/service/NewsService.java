package com.jm.online_store.service;

import com.jm.online_store.model.News;

import java.util.List;
import java.util.Optional;

public interface NewsService {

    List<News> findAll();

    void save(News news);

    Optional<News> findById(long id);

    boolean existsById(Long id);

    void updateById(News news);

    void deleteById(Long id);
}
