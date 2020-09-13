package com.jm.online_store.service.interf;

import com.jm.online_store.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface NewsService {

    List<News> findAll();

    Page<News> findAll(Pageable page);

    void save(News news);

    News findById(long id);

    boolean existsById(Long id);

    void update(News news);

    void deleteById(Long id);

    Page<News> getAllPublished(Pageable page);

    Page<News> getAllUnpublished(Pageable page);

    Page<News> getAllArchivedNews(Pageable page);
}
