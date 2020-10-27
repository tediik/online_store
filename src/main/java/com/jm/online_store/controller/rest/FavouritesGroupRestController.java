package com.jm.online_store.controller.rest;

import com.jm.online_store.model.FavouritesGroup;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.FavouritesGroupService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Рест контроллер для "Списков" Избранных товаров
 */
@RestController
@AllArgsConstructor
@RequestMapping("/customer/favouritesGroup")
public class FavouritesGroupRestController {
    private final FavouritesGroupService favouritesGroupService;
    private final UserService userService;

    /**
     * Получение списков избранных товаров залогиневшегося пользователя
     * @return
     */
    @GetMapping
    public ResponseEntity getFavouritesGroups() {
        User user = userService.getCurrentLoggedInUser();
        return ResponseEntity.ok(favouritesGroupService.findAllByUser(user));
    }

    /**
     * Сохраняем в БД новый список избранных товаров
     * @param favouritesGroup
     * @return возвращаем в точку вызова данные нового списка
     */
    @PostMapping
    public ResponseEntity addFavouritesGroups(@RequestBody FavouritesGroup favouritesGroup) {
        User user = userService.getCurrentLoggedInUser();
        favouritesGroup.setUser(user);
        favouritesGroupService.addFavouritesGroup(favouritesGroup);
        return ResponseEntity.ok(favouritesGroupService.getOneFavouritesGroupByUserAndByName(user, favouritesGroup.getName()));
    }

    /**
     * Удаляем из БД список избранных товаров
     * @param id идентификатор списка
     */
    @DeleteMapping("/{id}")
    public void deleteFavouritesGroups(@PathVariable("id") Long id) {
        favouritesGroupService.deleteById(id);
    }

    /**
     * Обновляем в БД имя списка избранных товаров
     * @param name новое имя
     * @param id идентификатор списка
     * @return  статус ответа 200
     */
    @PutMapping(value = "/customer/favouritesGroup/{id}")
    public ResponseEntity updateFavouritesGroups(@RequestBody String name, @PathVariable("id") Long id) {
        User user = userService.getCurrentLoggedInUser();
        FavouritesGroup favouritesGroup = favouritesGroupService.findById(id).orElseThrow();
        favouritesGroup.setName(name);
        favouritesGroupService.save(favouritesGroup);
        return ResponseEntity.ok().build();
    }
}
