package com.jm.online_store.controller.rest;

import com.jm.online_store.model.SharedNews;
import com.jm.online_store.model.dto.SharedNewsDto;
import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.SharedNewsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController для добавления новостей, которыми поделились пользователи
 * в социальных сетях
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/global/sharedNews")
@Api(description = "Rest controller for adding news, that users shared in social networks")
public class GlobalSharedNewsRestController {
    private final SharedNewsService sharedNewsService;
    private final CustomerService customerService;
    private final ModelMapper modelMapper;

    /**
     * Метод для добавления информации о том, какой новостью, какой пользователь,
     * в какой социальной сети поделился
     *
     * @param sharedNews содержит в себе id новости, которой поделились и название
     *                   социальной сети, в которой этой новостью поделились
     * @return ResponseEntity<String> возвращает статус ответа
     */
    @PostMapping
    @ApiOperation(value = "Adds information about which user shared which news in which social network. " +
            "SharedNews must include news id, which was shared and the name of social network, where was shared",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<String> addSharedNews(@RequestBody SharedNewsDto sharedNewsDto) {
        SharedNews sharedNews = modelMapper.map(sharedNewsDto, SharedNews.class);
        sharedNews.setCustomer(customerService.getCurrentLoggedInCustomer());
        sharedNewsService.addSharedNews(sharedNews);
        return ResponseEntity.ok().build();
    }

}
