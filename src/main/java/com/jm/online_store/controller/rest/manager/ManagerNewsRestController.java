package com.jm.online_store.controller.rest.manager;

import com.jm.online_store.enums.ResponseOperation;
import com.jm.online_store.model.News;
import com.jm.online_store.model.dto.NewsDto;
import com.jm.online_store.model.dto.NewsFilterDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.NewsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Рест контроллер для управления новостями из кабинете менеджера, а также публикации новостей
 * на странице новостей
 */
@PreAuthorize("hasAuthority('ROLE_MANAGER')")
@AllArgsConstructor
@RestController
@RequestMapping(value = "/api/manager/news")
@Api(description = "Rest controller for manage news from manager page, also for publishing news")
public class ManagerNewsRestController {

    private final NewsService newsService;
    private final ModelMapper modelMapper = new ModelMapper();
    private final Type listType = new TypeToken<List<NewsDto>>() {}.getType();

    /**
     * Mapping accepts @PathVariable {@link Long} id
     *
     * @param id - {@link Long} id of news entity
     * @return {@link ResponseEntity<NewsDto>} or ResponseEntity.notFound()
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "Get news by id",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses( value = {
            @ApiResponse(code = 200, message = "News has been found"),
            @ApiResponse(code = 404, message = "News hasn't been found")
    })
    public ResponseEntity<ResponseDto<NewsDto>> getNewsById(@PathVariable Long id) {
        NewsDto returnValue = modelMapper.map(newsService.findById(id), NewsDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }


    /**
     * Method returns all news
     *
     * @return List<NewsDto> возвращает список всех новостей из базы данных
     */
    @GetMapping("/all")
    @ApiOperation(value = "Method returns all news",
            authorizations = { @Authorization(value="jwtToken") })
    @ApiResponses( value = {
            @ApiResponse(code = 200, message = "News has been found"),
            @ApiResponse(code = 404, message = "News has not been found")
    })
    public ResponseEntity<ResponseDto<List<NewsDto>>> getAllNews() {
        List<NewsDto> returnValue = modelMapper.map(newsService.findAll(), listType);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }


    /**
     * Метод возвращает страницу новостей
     *
     * @param page параметры страницы
     * @return Page<News> возвращает страницу новостей
     */
    @PreAuthorize("permitAll()")
    @GetMapping("/page")
    @ApiOperation(value = "Method returns news page",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 200, message = "News page has found")
    public ResponseEntity<ResponseDto<Page<NewsDto>>> getPage(@PageableDefault Pageable page, NewsFilterDto filterDto) {
        return ResponseEntity.ok(new ResponseDto<>(true,
                modelMapper.map(newsService.findAll(page, filterDto), Page.class)));
    }

    /**
     * Method returns published news
     *
     * @return - ResponseEntity<List<News>>
     */
    @GetMapping("/published")
    @ApiOperation(value = "Method returns published news",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses( value = {
            @ApiResponse(code = 200, message = "Published news has been found"),
            @ApiResponse(code = 200, message = "Published news hasn't been found. Returns an empty list")
    })
    public ResponseEntity<ResponseDto<List<NewsDto>>> getAllPublishedNews() {
        List<News> listPubNewsFromService = newsService.getAllPublished();
        List<NewsDto> returnValue = modelMapper.map(listPubNewsFromService, listType);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Method returns unpublished news
     *
     * @return - ResponseEntity<List<News>>
     */
    @GetMapping("/unpublished")
    @ApiOperation(value = "Method returns unpublished news",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses( value = {
            @ApiResponse(code = 200, message = "Unpublished news has been found"),
            @ApiResponse(code = 200, message = "Unpublished news hasn't been found. Returns an empty list")
    })
    public ResponseEntity<ResponseDto<List<NewsDto>>> getAllUnpublishedNews() {
        List<News> listUnpubNewsFromService = newsService.getAllUnpublished();
        List<NewsDto> returnValue = modelMapper.map(listUnpubNewsFromService, listType);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Method returns archived news
     *
     * @return - ResponseEntity<List<News>>
     */
    @GetMapping("/archived")
    @ApiOperation(value = "Method returns list of archived news",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses( value = {
            @ApiResponse(code = 200, message = "archived news has been found"),
            @ApiResponse(code = 204, message = "archived news hasn't been found")
    })
    public ResponseEntity<ResponseDto<List<NewsDto>>> getAllArchivedNews() {
        List<NewsDto> returnValue = modelMapper.map(newsService.getAllArchivedNews(), listType);
            return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Метод сохраняет новости в базу данных
     *
     * @param news сущность для сохранения в базе данных
     * @return возвращает заполненную сущность клиенту
     */
    @PostMapping
    @ApiOperation(value = "Method to save news in database",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 201, message = "News has been successfully saved")
    public ResponseEntity<ResponseDto<NewsDto>> createNewsPost(@RequestBody NewsDto newsReq) {
        NewsDto returnValue = modelMapper.map(newsService.save(modelMapper.map(newsReq, News.class)), NewsDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Метод обновляет сущность в базе данных
     *
     * @param news сущность для сохранения в базе данных
     * @return возвращает обновленную сущность клиенту
     */
    @PutMapping
    @ApiOperation(value = "Method to update news in database",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses( value = {
            @ApiResponse(code = 200, message = "news has been successfully updated"),
            @ApiResponse(code = 400, message = "news hasn't been updated")
    })
    public ResponseEntity<ResponseDto<NewsDto>> updateNewsPost(@RequestBody NewsDto newsReq) {
        NewsDto returnValue = modelMapper.map(newsService.update(modelMapper.map(newsReq, News.class)), NewsDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Метод удаляет сушность из базы данных по уникальному идентификатору
     *
     * @param id уникальный идентификатор
     * @return возвращает ответ в виде строки с описанием результата
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "Method to delete news from database",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses( value = {
            @ApiResponse(code = 200, message = "News has been successfully deleted"),
            @ApiResponse(code = 404, message = "News hasn't been found")
    })
    public ResponseEntity<ResponseDto<String>> deleteNewsById(@PathVariable Long id) {
        newsService.deleteById(id);
        return ResponseEntity.ok(new ResponseDto<>(true,
                String.format(ResponseOperation.HAS_BEEN_DELETED.getMessage(), id)));
    }
}
