package com.jm.online_store.service.impl;

import com.jm.online_store.model.News;
import com.jm.online_store.repository.NewsRepository;
import com.jm.online_store.service.interf.NewsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Сервис класс, имплементация интерфейса {@link NewsService}
 * Содержит бизнес логику, использует методы репозитория {@link NewsRepository}
 */

@Service
@AllArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;

    /**
     * Метод без параметров, который просто тащит список новостей
     * @return List<News> возвращает список всех новостей
     */
    @Override
    public List<News> findAll() {
        return newsRepository.findAll();
    }

    /**
     * Метод сохраняет сущность, пришедшую в качестве параметра
     * @param news Сущность News c с заполненными полями
     */
    @Override
    public void save(News news) {
        newsRepository.save(news);
    }

    /**
     * Метод принимающий в качестве параметра идентификатор сущности,
     * ищет сущность по идентификатору и возвращает сущность News
     * @param id уникальный идентификатор сушности News
     * @return Optional<News> возвращаемое значение в виде сущности, обернутой в Optional
     */
    @Override
    public Optional<News> findById(long id) {
        return newsRepository.findById(id);
    }

    /**
     * Метод выполняет проверку существует ли сущность в базе.
     * @param id уникальный идентификатор сушности
     * @return Возвращает булево значение true или false
     */
    @Override
    public boolean existsById(Long id) {
        return newsRepository.existsById(id);
    }

    /**
     * Метод обновляет сущность News в базе данных по его уникальному идентификатору
     * @param news сушность для обновления в базе данных
     */
    public void updateById(News news) {
        newsRepository.save(news);
    }

    /**
     * Метод удаляет сущность News из базы данных по уникальному идентификатору
     * @param id уникальный идентификатор сущности News
     */
    @Override
    public void deleteById(Long id) {
        newsRepository.deleteById(id);
    }

    /**
     * Метод делающий выборку из базы данных по заданному параметру,
     * где LocalDateTime postingDate > LocalDateTime timeNow.
     * @param timeNow параметр типа LocalDateTime относительно которого делается выборка данных их базы данных
     * @return возвращает список еще неопубликованных новостей List<News>
     */
    @Override
    public List<News> findAllByPostingDateBefore(LocalDateTime timeNow) {
        return newsRepository.findAllByPostingDateBefore(timeNow);
    }

    /**
     * Метод делающий выборку из базы данных по заданному параметру,
     * где LocalDateTime postingDate <= LocalDateTime timeNow.
     * @param timeNow параметр типа LocalDateTime относительно которого делается выборка данных их базы данных
     * @return возвращает список опубликованных новостей List<News>
     */
    @Override
    public List<News> findAllByPostingDateAfter(LocalDateTime timeNow) {
        return newsRepository.findAllByPostingDateAfter(timeNow);
    }
}
