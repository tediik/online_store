package com.jm.online_store.service.interf;

import com.jm.online_store.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NewsService {

    Page<News> findAll(Pageable page);

    void save(News news);

    Optional<News> findById(long id);

    boolean existsById(Long id);

    void updateById(News news);

    void deleteById(Long id);

    List<News> findAllByPostingDateBefore(LocalDateTime timeNow);

    List<News> findAllByPostingDateAfter(LocalDateTime timeNow);
}
