package com.jm.online_store.service.impl;

import com.jm.online_store.exception.NewsNotFoundException;
import com.jm.online_store.model.News;
import com.jm.online_store.repository.NewsRepository;
import com.jm.online_store.service.interf.NewsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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
     *
     * @return List<News> возвращает список всех новостей
     */
    @Override
    public List<News> findAll() {
        List<News> allNews = newsRepository.findAll();
        if (allNews.isEmpty()) {
            throw new NewsNotFoundException("findAll returns empty List<News>");
        }
        return allNews;
    }

    /**
     * Метод сохраняет сущность, пришедшую в качестве параметра
     *
     * @param news Сущность News c с заполненными полями
     */
    @Override
    public void save(News news) {
        newsRepository.save(news);
    }

    /**
     * Method accept Long id as parameter and returns {@link News} entity
     *
     * @param id - {@link Long}
     * @return returns News entity or throws {@link NewsNotFoundException}
     */
    @Override
    public News findById(long id) {
        return newsRepository.findById(id).orElseThrow(() -> new NewsNotFoundException("There are no news with such id"));
    }

    /**
     * Метод выполняет проверку существует ли сущность в базе.
     *
     * @param id уникальный идентификатор сушности
     * @return Возвращает булево значение true или false
     */
    @Override
    public boolean existsById(Long id) {
        return newsRepository.existsById(id);
    }

    /**
     * Метод обновляет сущность News в базе данных и изменяет modifiedDate на сегодняшнюю дату
     *
     * @param news сушность для обновления в базе данных
     */
    public News update(News news) {
        news.setModifiedDate(LocalDate.now());
        return newsRepository.save(news);
    }

    /**
     * Метод удаляет сущность News из базы данных по уникальному идентификатору
     *
     * @param id уникальный идентификатор сущности News
     */
    @Override
    public void deleteById(Long id) {
        newsRepository.deleteById(id);
    }

    /**
     * Метод делающий выборку из базы данных по заданному параметру,
     * где LocalDateTime postingDate > LocalDateTime timeNow.
     *
     * @param timeNow параметр типа LocalDateTime относительно которого делается выборка данных их базы данных
     * @return возвращает список еще неопубликованных новостей List<News>
     */
    @Override
    public List<News> getAllPublished(LocalDate timeNow) {
        List<News> publishedNews = newsRepository.findAllByPostingDateBeforeAndArchivedEquals(timeNow, false);
        if (publishedNews.isEmpty()) {
            throw new NewsNotFoundException("There are no published news");
        }
        return publishedNews;
    }

    /**
     * Метод делающий выборку из базы данных по заданному параметру,
     * где LocalDateTime postingDate <= LocalDateTime timeNow.
     *
     * @param timeNow параметр типа LocalDateTime относительно которого делается выборка данных их базы данных
     * @return возвращает список опубликованных новостей List<News>
     */
    @Override
    public List<News> getAllUnpublished(LocalDate timeNow) {
        List<News> unpublishedNews = newsRepository.findAllByPostingDateAfterAndArchivedEquals(timeNow, false);
        if (unpublishedNews.isEmpty()) {
            throw new NewsNotFoundException("There are no unpublished news");
        }
        return unpublishedNews;
    }

    /**
     * Method returns list of all archived news ot throw {@link NewsNotFoundException}
     * @return - List<News>
     */
    @Override
    public List<News> getAllArchivedNews() {
        List<News> archivedNews = newsRepository.findAllByArchivedEquals(true);
        if (archivedNews.isEmpty()) {
            throw new NewsNotFoundException("There are no archived news");
        }
        return archivedNews;
    }
}
