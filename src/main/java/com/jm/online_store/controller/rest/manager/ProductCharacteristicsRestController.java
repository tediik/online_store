package com.jm.online_store.controller.rest.manager;

import com.jm.online_store.enums.ResponseOperation;
import com.jm.online_store.model.Characteristic;
import com.jm.online_store.model.ProductCharacteristic;
import com.jm.online_store.model.dto.CharacteristicDto;
import com.jm.online_store.model.dto.ProductCharacteristicDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.CharacteristicService;
import com.jm.online_store.service.interf.ProductCharacteristicService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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
import java.util.List;

/**
 * Контроллер для работы с характеристиками товаров
 */
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/manager/product")
public class ProductCharacteristicsRestController {

    private final CharacteristicService characteristicService;
    private final ProductCharacteristicService productCharacteristicService;
    private final ModelMapper modelMapper;
    private final Type listTypeCharDto = new TypeToken<List<CharacteristicDto>>() {}.getType();
    private final Type listTypeProdCharDto = new TypeToken<List<ProductCharacteristicDto>>() {}.getType();
    private final Type listTypeChar = new TypeToken<List<Characteristic>>() {}.getType();

    /**
     * Метод выводит список всех характеристик c описанием
     * или пустой список
     *
     * @return List<CharacteristicDto>> возвращает список характеристик
     */
    @GetMapping(value = "/characteristics/allCharacteristics")
    @ApiOperation(value = "return list of characteristics",
            authorizations = { @Authorization(value = "jwtToken")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Characteristics has been found"),
            @ApiResponse(code = 200, message = "Characteristics hasn't been found")
    })
    public ResponseEntity<ResponseDto<List<CharacteristicDto>>> findAll() {
        List<CharacteristicDto> returnValue = modelMapper.map(characteristicService.findAll(), listTypeCharDto);
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
            @ApiResponse(code = 201, message = "Characteristic has been created"),
            @ApiResponse(code = 400, message = "Characteristics hasn't been created")
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
     *
     * @param id - characteristic id (Long)
     * @return <CharacteristicDto> найденная сущность
     */
    @GetMapping(value = "/characteristic/{id}")
    @ApiOperation(value = "return characteristic by id",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Characteristic has been found"),
            @ApiResponse(code = 404, message = "Characteristic hasn't been found")
    })
    public ResponseEntity<ResponseDto<CharacteristicDto>> getCharacteristic(@PathVariable Long id) {
        Characteristic characteristic = characteristicService.getCharacteristicById(id);
        CharacteristicDto returnValue = modelMapper.map(characteristic, CharacteristicDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Метод обновляет сущность
     *
     * @param characteristicReq
     * @return <CharacteristicDto> обновленная сущность
     */
    @PutMapping(value = "/characteristics")
    @ApiOperation(value = "update characteristic",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Characteristic has been updated"),
            @ApiResponse(code = 404, message = "Characteristic hasn't been found")
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
     *
     * @param id       - id харакетристики для удаления
     * @param category - имя категории
     * @return String - описание результата операции
     */
    @DeleteMapping(value = "/characteristics/{id}/{category}")
    @ApiOperation(value = "delete characteristic by id",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Characteristic has been deleted"),
            @ApiResponse(code = 404, message = "Characteristic hasn't been found")
    })
    public ResponseEntity<ResponseDto<String>> deleteCharacteristic(@PathVariable Long id,
                                                               @PathVariable String category) {
        if (category.equals("default")) {
            characteristicService.deleteByID(id);
            log.debug("Characteristic with id: {}, was deleted successfully", id);

        } else {
            characteristicService.deleteByIDInSelectedCategory(id, category);

        }
        return ResponseEntity.ok(new ResponseDto<>(true, String.format(ResponseOperation.HAS_BEEN_DELETED.getMessage(), id,
                ResponseOperation.NO_ERROR.getMessage())));
    }


    /**
     * Метод, который возвращает характеристи для выбранной категории
     * или пустой список
     *
     * @param categoryId id нужной категории
     * @return List<CharacteristicDto> лист харктеристик
     */
    @GetMapping("/characteristics/{categoryId}")
    @ApiOperation(value = "return list of characteristics by id of category",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Characteristics has been found"),
            @ApiResponse(code = 200, message = "Characteristics hasn't been found. Returns empty list"),
            @ApiResponse(code = 404, message = "Category was not found")
    })
    public ResponseEntity<ResponseDto<List<CharacteristicDto>>> getCharacteristicsByCategory(@PathVariable Long categoryId) {
        List<CharacteristicDto> returnValue = modelMapper.map(characteristicService.findByCategoryId(categoryId), listTypeCharDto);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }

    /**
     * Метод, который возвращает характеристи для выбранной категории
     * или пустой список
     *
     * @param category имя нужной категории
     * @return List<CharacteristicDto> список харктеристик
     */
    @GetMapping("/characteristicsByCategoryName/{category}")
    @ApiOperation(value = "return list of characteristics by name of category",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Characteristics has been found"),
            @ApiResponse(code = 200, message = "Characteristics hasn't been found. Returns empty list"),
            @ApiResponse(code = 404, message = "Category hasn't been found")
    })
    public ResponseEntity<ResponseDto<List<CharacteristicDto>>> getCharacteristicsByCategoryName(@PathVariable String category) {
        List<CharacteristicDto> returnValue ;
        if (StringUtils.isNoneBlank(category)) {
            if (category.equals("default")) {
                returnValue = modelMapper.map(characteristicService.findAll(), listTypeCharDto);
            } else {
                returnValue = modelMapper.map(characteristicService.findByCategoryName(category), listTypeCharDto);
            }
        } else {
            return ResponseEntity.badRequest().build();
        }

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
            @ApiResponse(code = 201, message = "ProductCharacteristics have been added"),
            @ApiResponse(code = 200, message = "Characteristics haven't been found. Returns empty list"),
            @ApiResponse(code = 404, message = "Characteristic hasn't been found"),
            @ApiResponse(code = 404, message = "Product hasn't been found")
    })
    public ResponseEntity<ResponseDto<List<ProductCharacteristicDto>>> addCharacteristics(@RequestBody ProductCharacteristicDto[] productCharacteristicsDto,
                                                         @PathVariable String addedProductName) {
        List<ProductCharacteristicDto> returnValue;
        if (StringUtils.isNoneBlank(addedProductName)) {
            List<ProductCharacteristicDto> list = modelMapper.map(productCharacteristicsDto, listTypeProdCharDto);
            List<ProductCharacteristic> gotBack = productCharacteristicService.addProductCharacteristic(list, addedProductName);
            returnValue = modelMapper.map(gotBack, listTypeProdCharDto);
        } else {
            return ResponseEntity.badRequest().build();
        }
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
            authorizations = { @Authorization(value = "jwtToken")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Characteristics have been found"),
            @ApiResponse(code = 200, message = "Characteristics haven't been found. Returns empty list"),
            @ApiResponse(code = 404, message = "Categories haven't been found"),
            @ApiResponse(code = 400, message = "Bad request")
    })
    public ResponseEntity<ResponseDto<List<CharacteristicDto>>> findAllOtherExceptSelected(@PathVariable String categoryName) {
        List<Characteristic> gotBack = characteristicService.getAllCharacteristicsExceptSelectedCategory(categoryName);
        List<CharacteristicDto> returnValue =  modelMapper.map(gotBack, listTypeCharDto);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }


    /**
     * Метод добавляет характеристики выбранной категории
     *
     * @param selectedCategory название выбранной категории
     * @return ResponseEntity<List<CharacteristicDto>> Возвращает добавленные харакетристики с кодом ответа
     */
    @PostMapping(value = "/characteristics/addCharacteristicsToCategory/{selectedCategory}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "add new characteristics to selected category",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Characteristics have been found"),
            @ApiResponse(code = 200, message = "Characteristics haven't been found. Returns empty list"),
            @ApiResponse(code = 404, message = "Categories haven't been found"),
            @ApiResponse(code = 400, message = "Bad request")
    })
    public ResponseEntity<ResponseDto<List<CharacteristicDto>>> addCharacteristicsToCategory(@RequestBody CharacteristicDto[] characteristics,
                                                                             @PathVariable String selectedCategory) {
        List<CharacteristicDto> returnValue;
        if (StringUtils.isNoneBlank(selectedCategory)) {
            List<Characteristic> characteristicList = modelMapper.map(characteristics, listTypeChar);
            List<Characteristic> gotBack = characteristicService.addCharacteristicsToCategory(characteristicList, selectedCategory);
            returnValue = modelMapper.map(gotBack, listTypeCharDto);
        } else {
            return ResponseEntity.badRequest().build();
        }
        return new ResponseEntity<>(new ResponseDto<>(true, returnValue), HttpStatus.CREATED);
    }
}
