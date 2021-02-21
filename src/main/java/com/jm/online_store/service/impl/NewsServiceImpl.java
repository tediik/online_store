package com.jm.online_store.service.impl;

import com.jm.online_store.exception.constants.ExceptionConstants;
import com.jm.online_store.enums.ExceptionEnums;
import com.jm.online_store.exception.NewsNotFoundException;
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


/**
 * Сервис класс, имплементация интерфейса {@link NewsService}
 * Содержит бизнес логику, использует методы репозитория {@link NewsRepository}
 */
@Service
@AllArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;

    /**
     * Вытаскиваем список новостей.
     * @return List<News> возвращает список всех новостей
     */
    @Override
    public List<News> findAll() {
        return newsRepository.findAll();
    }

    /**
     * Метод извлекает страницу новостей
     * @param page параметры страницы
     * @return Page<News> возвращает страницу новостей
     */
    @Override
    public Page<News> findAll(Pageable page, NewsFilterDto filterDto) {
        Specification<News> spec = NewsSpec.get(filterDto);
        return newsRepository.findAll(spec, page);
    }

    /**
     * Метод сохраняет сущность News
     * @param news Сущность News c заполненными полями
     * @return News возвращает сохраненную новость
     */
    @Override
    public News save(News news) {
        return newsRepository.save(news);
    }

    /**
     * Method accept Long id as parameter and returns {@link News} entity
     * @param id - {@link Long}
     * @return returns News entity or throws {@link NewsNotFoundException}
     */
    @Override
    public News findById(long id) {
        return newsRepository.findById(id).orElseThrow(() ->
                new NewsNotFoundException(String.format(ExceptionConstants.WITH_SUCH_ID_NOT_FOUND, id)));

    }

    /**
     * Выполняем проверку, существует ли сущность в базе.
     * @param id уникальный идентификатор сушности
     * @return Возвращает булево значение true или false
     */
    @Override
    public boolean existsById(Long id) {
        return newsRepository.existsById(id);
    }



    /**
     * Обновляем сущность News в БД и изменяем modifiedDate на сегодняшнюю дату.
     * @param news сушность для обновления в базе данных
     * @return News возвращает обновленную новость
     */
    public News update(News news) {
        newsRepository.findById(news.getId()).orElseThrow(() ->
                new NewsNotFoundException(String.format(ExceptionEnums.NEWS +
                        ExceptionConstants.WITH_SUCH_ID_NOT_FOUND, news.getId())));
        news.setModifiedDate(LocalDate.now());
        return newsRepository.save(news);
    }

    /**
     * Удаляем сущность News из БД по ее уникальному идентификатору
     * @param id уникальный идентификатор сущности News
     * @return Возвращает булево значение true или false
     */
    @Override
    public boolean deleteById(Long id) {

        newsRepository.findById(id).orElseThrow(() ->
                new NewsNotFoundException(String.format(ExceptionEnums.NEWS.name() + ExceptionConstants.WITH_SUCH_ID_NOT_FOUND, id)));

        return true;
    }

    /**
     * Метод делающий выборку из базы данных по заданному параметру,
     * где LocalDate postingDate <= LocalDate timeNow.
     * @return возвращает список опубликованных новостей List<News>
     */
    @Override
    public List<News> getAllPublished() {
        return newsRepository.findAllByPostingDateBeforeAndArchivedEquals(LocalDate.now().plusDays(1), false);
    }

    /**
     * Метод делающий выборку из базы данных по заданному параметру,
     * где LocalDate postingDate > LocalDate timeNow.
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
