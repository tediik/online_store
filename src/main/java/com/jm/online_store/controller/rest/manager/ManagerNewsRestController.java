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

    /**
     * Mapping accepts @PathVariable {@link Long} id
     *
     * @param id - {@link Long} id of news entity
     * @return {@link ResponseEntity<News>} or ResponseEntity.notFound()
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "Get news by ID",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses( value = {
            @ApiResponse(code = 404, message = "News not found"),
            @ApiResponse(code = 200, message = "News was found")
    })
    public ResponseEntity<ResponseDto<NewsDto>> getNewsById(@PathVariable Long id) {
        News newsFromService = newsService.findById(id);
        NewsDto returnValue = modelMapper.map(newsFromService, NewsDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }




    /**
     * Method returns all news
     *
     * @return List<News> возвращает список всех новостей из базы данных
     */
    @GetMapping("/all")
    @ApiOperation(value = "Method returns all news",
            authorizations = { @Authorization(value="jwtToken") })
    @ApiResponses( value = {
            @ApiResponse(code = 404, message = "News not found"),
            @ApiResponse(code = 200, message = "News was found")
    })
    public ResponseEntity<ResponseDto<List<NewsDto>>> getAllNews() {
        List<News> listNewsFromService = newsService.findAll();
        Type listType = new TypeToken<List<NewsDto>>() {}.getType();
        List<NewsDto> returnValue = modelMapper.map(listNewsFromService, listType);
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
            authorizations = { @Authorization(value="jwtToken") })
    @ApiResponse(code = 200, message = "News page was found")
    public ResponseEntity<ResponseDto<Page<NewsDto>>> getPage(@PageableDefault Pageable page, NewsFilterDto filterDto) {
        Page<News> newsPageFromService = newsService.findAll(page, filterDto);
        Page<NewsDto> returnValue = modelMapper.map(newsPageFromService, Page.class); // пока не думал что сделать с сырым типом
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Method returns published news
     *
     * @return - ResponseEntity<List<News>>
     */
    @GetMapping("/published")
    @ApiOperation(value = "Method returns published news",
            authorizations = { @Authorization(value="jwtToken") })
    @ApiResponses( value = {
            @ApiResponse(code = 204, message = "Published news not found"),
            @ApiResponse(code = 200, message = "Published news was found")
    })
    public ResponseEntity<ResponseDto<List<NewsDto>>> getAllPublishedNews() {
        List<News> listPubNewsFromService = newsService.getAllPublished();
        Type listType = new TypeToken<List<NewsDto>>() {}.getType();
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
            authorizations = { @Authorization(value="jwtToken") })
    @ApiResponses( value = {
            @ApiResponse(code = 204, message = "Unpublished news not found"),
            @ApiResponse(code = 200, message = "Unpublished news was found")
    })
    public ResponseEntity<ResponseDto<List<NewsDto>>> getAllUnpublishedNews() {
        List<News> listUnpubNewsFromService = newsService.getAllUnpublished();
        Type listType = new TypeToken<List<NewsDto>>() {}.getType();
        List<NewsDto> returnValue = modelMapper.map(listUnpubNewsFromService, listType);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Method returns archived news
     *
     * @return - ResponseEntity<List<News>>
     */
    @GetMapping("/archived")
    @ApiOperation(value = "Method returns archived news",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses( value = {
            @ApiResponse(code = 204, message = "archived news not found"),
            @ApiResponse(code = 200, message = "archived news was found")
    })
    public ResponseEntity<ResponseDto<List<NewsDto>>> getAllArchivedNews() {
        List<News> listArchNewsFromService = newsService.getAllArchivedNews();
        Type listType = new TypeToken<List<NewsDto>>() {}.getType();
        List<NewsDto> returnValue = modelMapper.map(listArchNewsFromService, listType);
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
    @ApiResponse(code = 200, message = "News  saved in db")
    public ResponseEntity<ResponseDto<NewsDto>> createNewsPost(@RequestBody News news) {
        News newsFromService = newsService.save(news);
        NewsDto returnValue = modelMapper.map(newsFromService, NewsDto.class);
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
    @ApiResponse(code = 200, message = "News updated in db")
    public ResponseEntity<ResponseDto<NewsDto>> updateNewsPost(@RequestBody News news) {
        News newsFromService = newsService.update(news);
        NewsDto returnValue = modelMapper.map(newsFromService, NewsDto.class);
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
            @ApiResponse(code = 200, message = "News deleted"),
            @ApiResponse(code = 404, message = "News was not found")
    })
    public ResponseEntity<ResponseDto<String>> deleteNewsById(@PathVariable Long id) {
        return ResponseEntity.ok(new ResponseDto<>(true,
                String.format(ResponseOperation.HAS_BEEN_DELETED.getMessage(), id),
                ResponseOperation.NO_ERROR.getMessage()));
    }
}
