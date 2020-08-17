package com.jm.online_store.service;

import com.jm.online_store.model.News;
import com.jm.online_store.repository.NewsRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;

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
    public List<News> findAllByPostingDateBefore(LocalDateTime timeNow) {
        return newsRepository.findAllByPostingDateBefore(timeNow);
    }

    @Override
    public List<News> findAllByPostingDateAfter(LocalDateTime timeNow) {
        return newsRepository.findAllByPostingDateAfter(timeNow);
    }
}
