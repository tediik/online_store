package com.jm.online_store.service.interf;

import com.jm.online_store.model.News;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NewsService {

    List<News> findAll();

    void save(News news);

    News findById(long id);

    boolean existsById(Long id);

    void updateById(News news);

    void deleteById(Long id);

    List<News> findAllByPostingDateBefore(LocalDateTime timeNow);

    List<News> findAllByPostingDateAfter(LocalDateTime timeNow);

}
