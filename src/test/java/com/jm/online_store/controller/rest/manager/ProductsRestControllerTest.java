package com.jm.online_store.controller.rest.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jm.online_store.controller.ExceptionsHandler;
import com.jm.online_store.model.Characteristic;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.service.interf.CharacteristicService;
import com.jm.online_store.service.interf.ProductCharacteristicService;
import com.jm.online_store.service.interf.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ProductsRestControllerTest {
    private ProductService productService;
    private CategoriesService categoriesService;
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        productService = mock(ProductService.class);
        categoriesService = mock(CategoriesService.class);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ProductsRestController(productService, categoriesService))
                .setControllerAdvice(new ExceptionsHandler())
                .build();
    }


    @Test
    void handleFileUpload() {

    }

    @Test
    void testHandleFileUpload() {

    }

    @Test
    void findAll() {

    }

    @Test
    void getNotDeleteProducts() {

    }

    @Test
    void findProductById() {

    }

    @Test
    void addProduct() {
    }

    @Test
    void editProductM() {
    }

    @Test
    void editProductAndCategory() {
    }

    @Test
    void deleteProductById() {
    }

    @Test
    void restoreProductById() {
    }

    @Test
    void filterByCategory() {
    }

    @Test
    void filterByCategoryAndSort() {
    }

    @Test
    void filterByCategoryInDescOrder() {
    }

    @Test
    void getProductsReportAndExportToXlsx() {
    }
}
