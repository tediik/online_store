package com.jm.online_store.controller.rest.manager;

import com.jm.online_store.enums.ResponseOperation;
import com.jm.online_store.exception.CharacteristicNotFoundException;
import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.model.Categories;
import com.jm.online_store.model.Characteristic;
import com.jm.online_store.model.ProductCharacteristic;
import com.jm.online_store.model.dto.CategoriesDto;
import com.jm.online_store.model.dto.CharacteristicDto;
import com.jm.online_store.model.dto.ProductCharacteristicDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.model.dto.TopicsCategoryDto;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.service.interf.CharacteristicService;
import com.jm.online_store.service.interf.ProductCharacteristicService;
import com.jm.online_store.service.interf.ProductService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер для работы с характеристиками товаров
 */
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/manager/product")
public class ProductCharacteristicsRestController {

    private final ProductService productService;
    private final CharacteristicService characteristicService;
    private final ProductCharacteristicService productCharacteristicService;
    private final CategoriesService categoriesService;
    private final ModelMapper modelMapper = new ModelMapper();
    private final Type listType = new TypeToken<List<CharacteristicDto>>() {}.getType();
    private final Type listType2 = new TypeToken<List<ProductCharacteristicDto>>() {}.getType();

    /**
     * Метод выводит список всех характеристик c описанием
     *
     * @return List<CharacteristicDto>> возвращает список характеристик
     */
    @GetMapping(value = "/characteristics/allCharacteristics")
    @ApiOperation(value = "return list of characteristics",
            authorizations = { @Authorization(value = "jwtToken")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Characteristics were found"),
            @ApiResponse(code = 200, message = "Characteristics were not found")
    })
    public ResponseEntity<ResponseDto<List<CharacteristicDto>>> findAll() {
        List<CharacteristicDto> returnValue = modelMapper.map(characteristicService.findAll(), listType);
        return ResponseEntity.ok(new ResponseDto<>(true , returnValue));
    }

    /**
     * Метод добавляет характеристику
     *
     * @param characteristicReq характеристика для добавления
     * @return ResponseEntity<CharacteristicDto> Возвращает добавленную харакетристику с кодом ответа
     */
    @PostMapping(value = "/characteristics/addCharacteristic")
    @ApiOperation(value = "add new characteristic",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Characteristic was created"),
            @ApiResponse(code = 400, message = "Characteristics was not created")
    })
    public ResponseEntity<ResponseDto<CharacteristicDto>> addCharacteristic(@RequestBody CharacteristicDto characteristicReq) {
        if (!StringUtils.isNoneBlank(characteristicReq.getCharacteristicName())) {
            log.debug("EmptyCharacteristicName");
            return ResponseEntity.badRequest().build();
        }
        Characteristic characteristic = modelMapper.map(characteristicReq, Characteristic.class);
        CharacteristicDto returnValue = modelMapper.map(characteristicService.saveCharacteristic(characteristic), CharacteristicDto.class);
        return new ResponseEntity<>(new ResponseDto<>(true , returnValue), HttpStatus.CREATED);
    }

    /**
     * Метод возвращает характеристику по id или
     * бросает исключение CharacteristicNotFoundException
     *
     * @param id - characteristic id (Long)
     * @return ResponseEntity(characteristic, HttpStatus) {@link ResponseEntity}
     */
    @GetMapping(value = "/characteristic/{id}")
    @ApiOperation(value = "return characteristic by id",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Characteristic was found"),
            @ApiResponse(code = 404, message = "Characteristic was not found")
    })
    public ResponseEntity<ResponseDto<CharacteristicDto>> getCharacteristic(@PathVariable Long id) {
        Characteristic characteristic = characteristicService.getCharacteristicById(id);
        CharacteristicDto returnValue = modelMapper.map(characteristic, CharacteristicDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Метод обновляет сущность  или
     * бросает бросает исключение CharacteristicNotFoundException
     *
     * @param characteristicReq
     * @return ResponseEntity<ResponseDto<CharacteristicDto>> обновленная сущность
     */
    @PutMapping(value = "/characteristics")
    @ApiOperation(value = "update characteristic",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Characteristic was updated"),
            @ApiResponse(code = 404, message = "Characteristic was not found")
    })
    public ResponseEntity<ResponseDto<CharacteristicDto>> editCharacteristic(@RequestBody CharacteristicDto characteristicReq) {
        Characteristic characteristic = modelMapper.map(characteristicReq, Characteristic.class);
        Characteristic gotBack = characteristicService.updateCharacteristic(characteristic);
        CharacteristicDto returnValue = modelMapper.map(gotBack, CharacteristicDto.class);
        log.debug("Changes to characteristic with id: {} was successfully added", characteristic.getId());
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Метод для удаления характеристики
     * бросает бросает исключение CharacteristicNotFoundException
     *
     * @param id       - id харакетристики для удаления
     * @param category - имя категории
     * @return String - описание результата операции
     */
    @DeleteMapping(value = "/characteristics/{id}/{category}")
    @ApiOperation(value = "delete characteristic by id",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Characteristic was deleted"),
            @ApiResponse(code = 404, message = "Characteristic was not found")
    })
    public ResponseEntity<ResponseDto<String>> deleteCharacteristic(@PathVariable Long id,
                                                               @PathVariable String category) {
        if (category.equals("default")) {
            characteristicService.deleteByID(id);
            log.debug("Characteristic with id: {}, was deleted successfully", id);

        } else {
            characteristicService.deleteByIDInSelectedCategory(id, category);

        }
        return ResponseEntity.ok(new ResponseDto<>(true,
                String.format(ResponseOperation.HAS_BEEN_DELETED.getMessage(), id),
                ResponseOperation.NO_ERROR.getMessage()));
    }


    /**
     * Метод, который возвращает характеристи для выбранной категории или
     * бросает исключение CategoriesNotFoundException
     *
     * @param categoryId id нужной категории
     * @return List<CharacteristicDto> лист харктеристик
     */
    @GetMapping("/characteristics/{categoryId}")
    @ApiOperation(value = "return list of characteristics by id of category",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Characteristics were found"),
            @ApiResponse(code = 200, message = "Characteristics were not found. Returns empty list"),
            @ApiResponse(code = 404, message = "Category was not found")
    })
    public ResponseEntity<ResponseDto<List<CharacteristicDto>>> getCharacteristicsByCategory(@PathVariable Long categoryId) {
        List<CharacteristicDto> returnValue = modelMapper.map(characteristicService.findByCategoryId(categoryId), listType);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Метод, который возвращает характеристи для выбранной категории или
     * бросает исключение CategoriesNotFoundException
     *
     * @param category имя нужной категории
     * @return List<CharacteristicDto> лист харктеристик
     */
    @GetMapping("/characteristicsByCategoryName/{category}")
    @ApiOperation(value = "return list of characteristics by name of category",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Characteristics were found"),
            @ApiResponse(code = 200, message = "Characteristics were not found. Returns empty list"),
            @ApiResponse(code = 404, message = "Category was not found")
    })
    public ResponseEntity<ResponseDto<List<CharacteristicDto>>> getCharacteristicsByCategoryName(@PathVariable String category) {
        List<CharacteristicDto> returnValue ;
        if (category.equals("default")) {
            returnValue = modelMapper.map(characteristicService.findAll(), listType);
            return ResponseEntity.ok(new ResponseDto<>(true , returnValue));
        }
        returnValue = modelMapper.map(characteristicService.findByCategoryName(category), listType);
        return ResponseEntity.ok(new ResponseDto<>(true , returnValue));
    }



    /**
     * Метод добавляет характеристики, только что добавленному, товару
     *
     * @param addedProductName название добавленного товара
     * @return <List<ProductCharacteristicDto>> Возвращает список добавленных хар-к к
     * добавленному товару
     */
    @PostMapping(value = "/characteristics/addCharacteristics/{addedProductName}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "return added product with response code",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Characteristics were added"),
            @ApiResponse(code = 404, message = "Characteristic was not found")
    })
    public ResponseEntity<ResponseDto<List<ProductCharacteristicDto>>> addCharacteristics(@RequestBody ProductCharacteristicDto[] productCharacteristicsDto,
                                                         @PathVariable String addedProductName) {
        List<ProductCharacteristicDto> list = modelMapper.map(productCharacteristicsDto,listType2);
        List<ProductCharacteristic> gotBack = productCharacteristicService.addProductCharacteristic(list, addedProductName);
        List<ProductCharacteristicDto> returnValue = modelMapper.map(gotBack, listType2);
        return new ResponseEntity<>(new ResponseDto<>(true, returnValue), HttpStatus.CREATED);
    }

    /**
     * Метод возвращает список всех характеристик, кроме характеристик выбранной категории
     *
     * @param categoryName наименование хаарктеристики
     * @return List<Characteristic>> возвращает список характеристик
     */
    @GetMapping(value = "/characteristics/otherThenSelected/{categoryName}")
    @ApiOperation(value = "return list of characteristics except the selected category",
            authorizations = { @Authorization(value = "jwtToken") })
    public List<Characteristic> findAllOtherThenSelected(@PathVariable String categoryName) {
        List<Characteristic> characteristicsSelectedCategoty = characteristicService.findByCategoryName(categoryName);
        List<Characteristic> allCharacteristics = characteristicService.findAll();
        allCharacteristics.removeAll(characteristicsSelectedCategoty);
        return allCharacteristics;
    }

    /**
     * Метод добавляет характеристики выбранной категории
     *
     * @param selectedCategory название выбранной категории
     * @return ResponseEntity<List < Characteristic>> Возвращает добавленные харакетристики с кодом ответа
     */
    @PostMapping(value = "/characteristics/addCharacteristicsToCategory/{selectedCategory}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "add new characteristics to selected category",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<List<Characteristic>> addCharacteristicsToCategory(@RequestBody Characteristic[] characteristics,
                                                                             @PathVariable String selectedCategory) {
        Categories category = categoriesService.getCategoryByCategoryName(selectedCategory).get();
        List<Characteristic> characteristicsSelectedCategory = category.getCharacteristics();
        for (Characteristic characteristic : characteristics) {
            characteristicsSelectedCategory.add(characteristicService.findByCharacteristicName(characteristic.getCharacteristicName())
                    .orElseThrow(CharacteristicNotFoundException::new));
        }
        category.setCharacteristics(characteristicsSelectedCategory);
        categoriesService.saveCategory(category);

        return ResponseEntity.ok(characteristicsSelectedCategory);
    }
}
