package com.jm.online_store.service.interf;

import com.jm.online_store.model.News;
import com.jm.online_store.model.dto.NewsFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface NewsService {

    List<News> findAll();

    Page<News> findAll(Pageable page, NewsFilterDto filterDto);

    void save(News news);

    News findById(long id);

    boolean existsById(Long id);

    News update(News news);

    boolean deleteById(Long id);

    List<News> getAllPublished();

    List<News> getAllUnpublished();

    List<News> getAllArchivedNews();
}
