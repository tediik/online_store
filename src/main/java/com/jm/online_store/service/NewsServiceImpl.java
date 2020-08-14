package com.jm.online_store.service;

import com.jm.online_store.model.News;
import com.jm.online_store.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    NewsRepository newsRepository;

    @Override
    public List<News> findAll() {

        return newsRepository.findAll();
    }

    @Override
    public void save(News news) {
        newsRepository.save(news);
    }

    @Override
    public Optional<News> findById(long id) {
        return newsRepository.findById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return newsRepository.existsById(id);
    }

    public void updateById(News news) {
        newsRepository.save(news);
    }

    @Override
    public void deleteById(Long id) {
        newsRepository.deleteById(id);
    }

    @Override
    public List<News> findAllWithPostingDateTimeBefore(LocalDateTime postingDate) {

        return newsRepository.findAllWithPostingDateTimeBefore(postingDate);
    }

    @Override
    public List<News> findAllWithPostingDateTimeAfter(LocalDateTime postingDate) {

        return newsRepository.findAllWithPostingDateTimeAfter(postingDate);
    }
}
