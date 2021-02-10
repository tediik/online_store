package com.jm.online_store.service.impl;

import com.jm.online_store.exception.newsService.NewsExceptionConstants;
import com.jm.online_store.exception.newsService.NewsServiceException;
import com.jm.online_store.model.News;
import com.jm.online_store.model.dto.NewsFilterDto;
import com.jm.online_store.repository.NewsRepository;
import com.jm.online_store.service.interf.NewsService;
import com.jm.online_store.service.spec.NewsSpec;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
     *
     * @return List<News> возвращает список всех новостей
     */
    @Override
    public List<News> findAll() {
        List<News> allNews = newsRepository.findAll();
        if (allNews.isEmpty()) {
            throw new NewsServiceException(NewsExceptionConstants.NO_NEWS_YET);
        }
        return allNews;
    }

    /**
     * Метод извлекает страницу новостей
     *
     * @param page параметры страницы
     * @return Page<News> возвращает страницу новостей
     */
    @Override
    public Page<News> findAll(Pageable page, NewsFilterDto filterDto) {
        Specification<News> spec = NewsSpec.get(filterDto);
        return newsRepository.findAll(spec, page);
    }

    /**
     * Метод сохраняет сущность, пришедшую в качестве параметра
     *
     * @param news Сущность News c с заполненными полями
     */
    @Override
    public News save(News news) {
        return newsRepository.save(news);
    }

    /**
     * Method accept Long id as parameter and returns {@link News} entity
     *
     * @param id - {@link Long}
     * @return returns News entity or throws {@link com.jm.online_store.exception.NewsNotFoundException}
     */
    @Override
    public News findById(long id) {
        if (newsRepository.findById(id).isEmpty()) {
             throw new NewsServiceException(String.format(NewsExceptionConstants.NO_NEWS_WITH_SUCH_ID, id));
        }
        return newsRepository.findById(id).get();
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
    public boolean deleteById(Long id) {
        Optional<News> optionalNews = newsRepository.findById(id);
        if (optionalNews.isEmpty()) {
            throw new NewsServiceException(String.format(NewsExceptionConstants.NO_NEWS_WITH_SUCH_ID, id));
        }
        newsRepository.deleteById(id);
        return true;
    }

    /**
     * Метод делающий выборку из базы данных по заданному параметру,
     * где LocalDate postingDate <= LocalDate timeNow.
     *
     * @return возвращает список опубликованных новостей List<News>
     */
    @Override
    public List<News> getAllPublished() {
        List<News> publishedNews = newsRepository.findAllByPostingDateBeforeAndArchivedEquals(LocalDate.now().plusDays(1), false);
        if (publishedNews.isEmpty()) {
            throw new NewsServiceException(NewsExceptionConstants.NO_PUBLISHED_NEWS);
        }
        return publishedNews;
    }

    /**
     * Метод делающий выборку из базы данных по заданному параметру,
     * где LocalDate postingDate > LocalDate timeNow.
     *
     * @return возвращает список еще неопубликованных новостей List<News>
     */
    @Override
    public List<News> getAllUnpublished() {
        List<News> unpublishedNews = newsRepository.findAllByPostingDateAfterAndArchivedEquals(LocalDate.now(), false);
        if (!unpublishedNews.isEmpty()) {
            throw new NewsServiceException(NewsExceptionConstants.NO_UNPUBLISHED_NEWS);
        }
        return unpublishedNews;
    }

    /**
     * Method returns list of all archived news ot throw {@link com.jm.online_store.exception.NewsNotFoundException}
     * @return - List<News>
     */
    @Override
    public List<News> getAllArchivedNews() {
        List<News> archivedNews = newsRepository.findAllByArchivedEquals(true);
        if (archivedNews.isEmpty()) {
            throw new NewsServiceException(NewsExceptionConstants.NO_ARCHIVED_NEWS);
        }
        return archivedNews;
    }
}
