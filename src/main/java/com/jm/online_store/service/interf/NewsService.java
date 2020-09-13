package com.jm.online_store.service.interf;

import com.jm.online_store.model.News;

import java.time.LocalDate;
import java.util.List;

public interface NewsService {

    List<News> findAll();

    void save(News news);

    News findById(long id);

    boolean existsById(Long id);

    News update(News news);

    void deleteById(Long id);

    List<News> getAllPublished(LocalDate timeNow);

    List<News> getAllUnpublished(LocalDate timeNow);

    List<News> getAllArchivedNews();
}
