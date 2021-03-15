package com.jm.online_store.controller.rest.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jm.online_store.controller.ExceptionsHandler;
import com.jm.online_store.model.Characteristic;
import com.jm.online_store.model.dto.CharacteristicDto;
import com.jm.online_store.service.interf.CharacteristicService;
import com.jm.online_store.service.interf.ProductCharacteristicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductCharacteristicsRestControllerTest {
    private CharacteristicService characteristicService;
    private ProductCharacteristicService productCharacteristicService;
    private ModelMapper modelMapper;
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private List<Characteristic> characteristics;
    private final static String END_POINT = "/api/manager/product";

    @BeforeEach
    void setUp() {
        modelMapper = new ModelMapper();
        objectMapper = new ObjectMapper();
        characteristicService = mock(CharacteristicService.class);
        productCharacteristicService = mock(ProductCharacteristicService.class);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ProductCharacteristicsRestController(characteristicService,productCharacteristicService, modelMapper))
                .setControllerAdvice(new ExceptionsHandler())
                .build();
        characteristics = Arrays.asList(
                new Characteristic(1L, "black"),
                new Characteristic(2L, "red")
        );
    }

    @Test
    @DisplayName("find all characteristics")
    void findAll() throws Exception {
        when(characteristicService.findAll()).thenReturn(characteristics);
        mockMvc.perform(get(END_POINT + "/characteristics/allCharacteristics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(characteristics.size())))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        characteristics.get(0).getId().intValue(),
                        characteristics.get(1).getId().intValue())))
                .andExpect(jsonPath("$.data[*].characteristicName", containsInAnyOrder(
                        characteristics.get(0).getCharacteristicName(),
                        characteristics.get(1).getCharacteristicName())));
    }

    @Test
    @DisplayName("add characteristic")
    void addCharacteristic() throws Exception {
        when(characteristicService.saveCharacteristic(characteristics.get(0))).thenReturn(characteristics.get(0));
        mockMvc.perform(post(END_POINT + "/characteristics/addCharacteristic")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(toCharacteristicDto(characteristics.get(0)))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(characteristics.get(0).getId()))
                .andExpect(jsonPath("$.data.characteristicName").value(characteristics.get(0).getCharacteristicName()));
    }

    @Test
    @DisplayName("get characteristic by id")
    void getCharacteristic() throws Exception {
        when(characteristicService.getCharacteristicById(anyLong())).thenReturn(characteristics.get(0));
        mockMvc.perform(get(END_POINT + "/characteristic/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(characteristics.get(0).getId().intValue()))
                .andExpect(jsonPath("$.data.characteristicName").value(characteristics.get(0).getCharacteristicName()));
    }

    @Test
    @DisplayName("edit characteristic")
    void editCharacteristic() throws Exception {
        when(characteristicService.updateCharacteristic(characteristics.get(0))).thenReturn(characteristics.get(1));
        mockMvc.perform(put(END_POINT + "/characteristics")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(toCharacteristicDto(characteristics.get(0)))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(characteristics.get(1).getId()))
                .andExpect(jsonPath("$.data.characteristicName").value(characteristics.get(1).getCharacteristicName()));
    }

    @Test
    @DisplayName("delete characteristic with id and category name")
    void deleteCharacteristicWithIdAndCategoryName() throws Exception {
        doNothing().when(characteristicService).deleteByIDInSelectedCategory(1L, "someCategory");
        mockMvc.perform(delete(END_POINT + "/characteristics/{id}/{category}", 1L, "someCategory")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("delete characteristic by id with default category name")
    void deleteCharacteristicByIdWithDefaultCategoryName() throws Exception {
        doNothing().when(characteristicService).deleteByID(anyLong());
        mockMvc.perform(delete(END_POINT + "/characteristics/{id}/{category}", anyLong(), "default")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("get characteristics by category")
    void getCharacteristicsByCategory() throws Exception {
        when(characteristicService.findByCategoryId(1L)).thenReturn(characteristics);
        mockMvc.perform(get(END_POINT + "/characteristics/{categoryId}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(characteristics.size())))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        characteristics.get(0).getId().intValue(),
                        characteristics.get(1).getId().intValue())))
                .andExpect(jsonPath("$.data[*].characteristicName", containsInAnyOrder(
                        characteristics.get(0).getCharacteristicName(),
                        characteristics.get(1).getCharacteristicName())));
    }

    @Test
    @DisplayName("get characteristics by category name with default category name")
    void getCharacteristicsByCategoryNameWithDefaultCategoryName() throws Exception {
        when(characteristicService.findAll()).thenReturn(characteristics);
        mockMvc.perform(get(END_POINT + "/characteristicsByCategoryName/{category}" , "default")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(characteristics.size())))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        characteristics.get(0).getId().intValue(),
                        characteristics.get(1).getId().intValue())))
                .andExpect(jsonPath("$.data[*].characteristicName", containsInAnyOrder(
                        characteristics.get(0).getCharacteristicName(),
                        characteristics.get(1).getCharacteristicName())));
    }

    @Test
    @DisplayName("get characteristics by category name with default category name")
    void getCharacteristicsByCategoryNameWithNotDefaultCategoryName() throws Exception {
        when(characteristicService.findByCategoryName("not default")).thenReturn(characteristics);
        mockMvc.perform(get(END_POINT + "/characteristicsByCategoryName/{category}" , "not default")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(characteristics.size())))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        characteristics.get(0).getId().intValue(),
                        characteristics.get(1).getId().intValue())))
                .andExpect(jsonPath("$.data[*].characteristicName", containsInAnyOrder(
                        characteristics.get(0).getCharacteristicName(),
                        characteristics.get(1).getCharacteristicName())));
    }

    @Test
    @DisplayName("save characteristics")
    void addCharacteristics() throws Exception {
        when(characteristicService.saveCharacteristic(characteristics.get(0))).thenReturn(characteristics.get(0));
        mockMvc.perform(post(END_POINT + "/characteristics/addCharacteristic")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(toCharacteristicDto(characteristics.get(0)))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(characteristics.get(0).getId()))
                .andExpect(jsonPath("$.data.characteristicName").value(characteristics.get(0).getCharacteristicName()));
    }

    @Test
    @DisplayName("find all other except selected characteristics")
    void findAllOtherExceptSelected() throws Exception {
        when(characteristicService.getAllCharacteristicsExceptSelectedCategory("someString")).thenReturn(characteristics);
        mockMvc.perform(get(END_POINT + "/characteristics/otherThenSelected/{categoryName}", "someString")
        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(characteristics.size())))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        characteristics.get(0).getId().intValue(),
                        characteristics.get(1).getId().intValue())))
                .andExpect(jsonPath("$.data[*].characteristicName", containsInAnyOrder(
                        characteristics.get(0).getCharacteristicName(),
                        characteristics.get(1).getCharacteristicName())));
    }

    @Test
    @DisplayName("add characteristics to category")
    void addCharacteristicsToCategory() throws Exception {
        when(characteristicService.addCharacteristicsToCategory(characteristics, "someCategory")).thenReturn(characteristics);
        mockMvc.perform(post(END_POINT + "/characteristics/addCharacteristicsToCategory/{selectedCategory}", "someCategory")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(toCharacteristicDto(characteristics))))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        characteristics.get(0).getId().intValue(),
                        characteristics.get(1).getId().intValue())))
                .andExpect(jsonPath("$.data[*].characteristicName", containsInAnyOrder(
                        characteristics.get(0).getCharacteristicName(),
                        characteristics.get(1).getCharacteristicName())));
    }

    private List<CharacteristicDto> toCharacteristicDto(List<Characteristic> characteristics) {
        Type listTypeCharDto = new TypeToken<List<CharacteristicDto>>() {}.getType();
        return modelMapper.map(characteristics, listTypeCharDto);
    }

    private CharacteristicDto toCharacteristicDto(Characteristic characteristic) {
        return modelMapper.map(characteristic, CharacteristicDto.class);
    }
}
