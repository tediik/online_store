package com.jm.online_store.controller.rest;

import com.jm.online_store.model.SharedNews;
import com.jm.online_store.service.interf.SharedNewsService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/global/api/sharedNews")
public class GlobalSharedNewsRestController {
    private final SharedNewsService sharedNewsService;
    private final UserService userService;

    /**
     * Метод для добавления информации о том, какой новостью, какой пользователь,
     * в какой социальной сети поделился
     *
     * @param sharedNews содержит в себе id новости, которой поделились и название
     *                   социальной сети, в которой этой новостью поделились
     * @return ResponseEntity<String> возвращает статус ответа
     */
    @PostMapping
    public ResponseEntity<String> addSharedNews(@RequestBody SharedNews sharedNews) {
        sharedNews.setUser(userService.getCurrentLoggedInUser());
        sharedNewsService.addSharedNews(sharedNews);
        return ResponseEntity.ok().build();
    }

}
