package com.jm.online_store.controller.rest.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jm.online_store.controller.ExceptionsHandler;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.Stock;
import com.jm.online_store.model.dto.ProductDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.StockService;
import lombok.NonNull;
import org.apache.commons.compress.utils.IOUtils;
import org.checkerframework.checker.nullness.Opt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PictureProductControllerTest {

    private ProductService productService;
    private static final String END_POINT = "/api/product";
    private MockMvc mockMvc;
    private MockMultipartFile pictureFile;
    private Product product1 = new Product(1L, "PRODUCT1", 1.0, 2, 2.3, "TYPE1");
    private Product product2 = new Product(2L,"PRODUCT2", 2.0, 3, 4.3, "TYPE2");


    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new PictureProductController(productService))
                .setControllerAdvice(new ExceptionsHandler())
                .build();
    }

    @Test
    @DisplayName("edit picture")
    void editPicture() throws Exception {
        MockMultipartFile pictureFile = new MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(END_POINT + "/upload/picture/{id}", 1L);
        when(productService.findProductById(1L)).thenReturn(Optional.ofNullable(product2));
        when(productService.editProduct(product1)).thenReturn(product2);
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });
        mockMvc.perform(builder
                .file("pictureFile", pictureFile.getBytes()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("delete picture")
    void deletePicture() {

    }
}
