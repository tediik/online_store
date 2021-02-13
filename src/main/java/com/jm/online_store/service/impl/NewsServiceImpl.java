package com.jm.online_store.service.impl;

import com.jm.online_store.exception.newsService.NewsExceptionConstants;
import com.jm.online_store.exception.newsService.NewsNotFoundException;
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
        return newsRepository.findAll();
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
     * @return returns News entity or throws {@link NewsNotFoundException}
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
        if (newsRepository.findById(news.getId()).isEmpty()) {
            throw new NewsNotFoundException(String.format(NewsExceptionConstants.NO_NEWS_WITH_SUCH_ID, news.getId()));
        }
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
            throw new NewsNotFoundException(String.format(NewsExceptionConstants.NO_NEWS_WITH_SUCH_ID, id));
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
        return newsRepository.findAllByPostingDateBeforeAndArchivedEquals(LocalDate.now().plusDays(1), false);
    }

    /**
     * Метод делающий выборку из базы данных по заданному параметру,
     * где LocalDate postingDate > LocalDate timeNow.
     *
     * @return возвращает список еще неопубликованных новостей List<News>
     */
    @Override
    public List<News> getAllUnpublished() {
        return newsRepository.findAllByPostingDateAfterAndArchivedEquals(LocalDate.now(), false);
    }

    /**
     * Method returns list of all archived news ot throw {@link NewsNotFoundException}
     * @return - List<News>
     */
    @Override
    public List<News> getAllArchivedNews() {
        return newsRepository.findAllByArchivedEquals(true);
    }
}
