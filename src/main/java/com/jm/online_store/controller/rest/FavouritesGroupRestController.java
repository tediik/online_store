package com.jm.online_store.controller.rest;

import com.jm.online_store.enums.ResponseOperation;
import com.jm.online_store.model.FavouritesGroup;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.FavouritesGroupDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.FavouritesGroupService;
import com.jm.online_store.service.interf.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
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
import java.util.List;

/**
 * Рест контроллер для "Списков" Избранных товаров
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/customer/favouritesGroup")
@Api(description = "Rest controller for the \"lists\" of favourites products - CRUD operations")
public class FavouritesGroupRestController {
    private final FavouritesGroupService favouritesGroupService;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final Type listType = new TypeToken<List<FavouritesGroupDto>>() {}.getType();

    /**
     * Получение избранной группы по Id
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "gets favourite group by id")
    public ResponseEntity<ResponseDto<FavouritesGroupDto>> getFavouritesGroupById(@PathVariable Long id) {
        return new ResponseEntity<>(new ResponseDto<>(
                true, modelMapper.map(favouritesGroupService.findById(id), FavouritesGroupDto.class)), HttpStatus.OK);
    }

    /**
     * Получение списков избранных товаров залогиневшегося пользователя
     * @return
     */
    @GetMapping
    @ApiOperation(value = "gets favourites products for the current logged in user",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<List<FavouritesGroupDto>>>  getFavouritesGroups() {
        User user = userService.getCurrentLoggedInUser();
        List<FavouritesGroupDto> returnValue = modelMapper.map(favouritesGroupService.findAllByUser(user), listType);
        return new ResponseEntity<>(new ResponseDto<>(true, returnValue), HttpStatus.OK);
    }

    /**
     * Сохраняем в БД новый список избранных товаров
     * @param favouritesGroup
     * @return возвращаем в точку вызова данные нового списка
     */
    @PostMapping
    @ApiOperation(value = "saves new list of favourite products",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<List<FavouritesGroupDto>>>  addFavouritesGroups(@RequestBody FavouritesGroup favouritesGroup) {
        User user = userService.getCurrentLoggedInUser();
        favouritesGroup.setUser(user);
        favouritesGroupService.addFavouritesGroup(favouritesGroup);
        List<FavouritesGroupDto> returnValue = modelMapper.map(favouritesGroupService.getOneFavouritesGroupByUserAndByName(user, favouritesGroup.getName()), listType);
        return new ResponseEntity<>(new ResponseDto<>(true, returnValue), HttpStatus.OK);
    }

    /**
     * Удаляем из БД список избранных товаров
     * @param id идентификатор списка
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "deletes list of favorite goods by its id",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<String>> deleteFavouritesGroups(@PathVariable("id") Long id) {
        favouritesGroupService.deleteById(id);
        return new ResponseEntity<>(new ResponseDto<>(true, "Favourites product list successful deleted", ResponseOperation.NO_ERROR.getMessage()), HttpStatus.OK);
    }

    /**
     * Обновляем в БД имя списка избранных товаров
     * @param name новое имя
     * @param id идентификатор списка
     * @return статус ответа 200
     */
    @PutMapping(value = "/{id}")
    @ApiOperation(value = "updates list of favorite goods by its id",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<FavouritesGroupDto>>  updateFavouritesGroups(@RequestBody String name, @PathVariable("id") Long id) {
        User user = userService.getCurrentLoggedInUser();
        FavouritesGroup favouritesGroup = favouritesGroupService.findById(id).orElseThrow();
        favouritesGroup.setName(name);
        favouritesGroupService.save(favouritesGroup);
        return new ResponseEntity<>(new ResponseDto<>(true, modelMapper.map(favouritesGroup, FavouritesGroupDto.class)), HttpStatus.OK);
    }
}
