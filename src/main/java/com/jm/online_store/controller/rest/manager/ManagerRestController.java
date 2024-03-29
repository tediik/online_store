package com.jm.online_store.controller.rest.manager;

import com.jm.online_store.enums.Response;
import com.jm.online_store.model.News;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.NewsDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.model.dto.UserDto;
import com.jm.online_store.service.interf.NewsService;
import com.jm.online_store.service.interf.OrderService;
import com.jm.online_store.service.interf.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;

/**
 * Рест контроллер для управления новостями из кабинете менеджера, а также публикации новостей
 * на странице новостей
 */
@AllArgsConstructor
@RestController
@RequestMapping(value = "/api/manager")
@Slf4j
@Api(description = "Rest controller for manage and publish news from manager page")
public class ManagerRestController {

    private final NewsService newsService;
    private final OrderService orderService;
    private final UserService userService;
    private final Type listType = new TypeToken<List<NewsDto>>() {}.getType();
    private final ModelMapper modelMapper;

    /**
     * Метод возвращающий залогиненного юзера. Работает при включенной сессии.
     * @return authUser возвращает юзера из базы данных
     */
    @GetMapping(value = "/authUser")
    @ApiOperation(value = "receive authenticated user from manager page")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "user has been found"),
            @ApiResponse(code = 404, message = "user hasn't been found")
    })
    public ResponseEntity<ResponseDto<UserDto>> showAuthUserInfo() {
        User authUser = userService.getCurrentLoggedInUser();
        return ResponseEntity.ok(new ResponseDto<>(true, modelMapper.map(authUser, UserDto.class)));
    }

    /**
     * Метод возвращающий всписок всех новостей
     * или пустой список
     * @return List<NewsDto> возвращает список всех новстей из базы данных
     */
    @GetMapping("/news")
    @ApiOperation(value = "Get list of all news",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "News has been found"),
            @ApiResponse(code = 200, message = "News has not been found. Returns empty list")
    })
    public ResponseEntity<ResponseDto<List<NewsDto>>> getAllNews() {
        List<NewsDto> returnValue = modelMapper.map(newsService.findAll(), listType);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Метод сохраняет новости в базу данных
     * @param newsReq сущность для сохранения в базе данных
     * @return NewsDto возвращает заполненную сущность клиенту
     */
    @PostMapping("/news/post")
    @ApiOperation(value = "Method for save news in database",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponse(code = 201, message = "News has been created")
    public ResponseEntity<ResponseDto<NewsDto>> newsPost(@RequestBody NewsDto newsReq) {
        if (newsReq.getPostingDate() == null || newsReq.getPostingDate().isBefore(LocalDate.now())) {
            newsReq.setPostingDate(LocalDate.now());
        }
        News newsFromService = newsService.save(modelMapper.map(newsReq, News.class));
        NewsDto returnValue = modelMapper.map(newsFromService, NewsDto.class);
        return new ResponseEntity<>(new ResponseDto<>(true, returnValue), HttpStatus.CREATED);
    }

    /**
     * Метод обновляет сущность в базе данных
     * @param newsReq сущность для сохранения в базе данных
     * @return возвращает обновленную сущность клиенту
     */
    @PutMapping("/news/update")
    @ApiOperation(value = "Method for update news in database",
            authorizations = {@Authorization(value = "jwtToken")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "News has been updated"),
            @ApiResponse(code = 404, message = "News has not been found")
    })
    public ResponseEntity<ResponseDto<NewsDto>> newsUpdate(@RequestBody NewsDto newsReq) {
        if (newsReq.getPostingDate() == null || newsReq.getPostingDate().isBefore(LocalDate.now())) {
            newsReq.setPostingDate(LocalDate.now());
        }
        News toService = modelMapper.map(newsReq, News.class);
        News newsFromService = newsService.update(toService);
        NewsDto returnValue = modelMapper.map(newsFromService, NewsDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Метод удаляет сушность из базы данных по уникальному идентификатору
     * @param id уникальный идентификатор
     * @return возвращает ответ в виде строки с описанием результата
     */
    @DeleteMapping("/news/{id}/delete")
    @ApiOperation(value = "Method for delete news in database by ID",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "user has been successfully deleted"),
            @ApiResponse(code = 404, message = "user hasn't been found")
    })
    public ResponseEntity<ResponseDto<String>> newsDelete(@PathVariable Long id) {
        newsService.deleteById(id);
        return ResponseEntity.ok(new ResponseDto<>(true ,
                String.format(Response.HAS_BEEN_DELETED.getText(), id),
                Response.NO_ERROR.getText()));
    }

}
