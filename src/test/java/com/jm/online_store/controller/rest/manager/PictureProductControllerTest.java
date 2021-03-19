package com.jm.online_store.controller.rest.manager;

import com.jm.online_store.controller.ExceptionsHandler;
import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.model.Product;
import com.jm.online_store.service.interf.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.io.File;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PictureProductControllerTest {
    private ProductService productService;
    private static final String END_POINT = "/api/product";
    private MockMvc mockMvc;
    private Product product1;
    private Product product2;
    private static final String loadPictureFrom = ".." + File.separator + "uploads" + File.separator + "images" + File.separator + "products" + File.separator;

    @BeforeEach
    void setUp() {
        productService = mock(ProductService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new PictureProductController(productService))
                .setControllerAdvice(new ExceptionsHandler())
                .build();
        product1 = new Product(1L, "PRODUCT1", 1.0, 2, 2.3, "TYPE1", Collections.singletonList(loadPictureFrom + "defaultPictureProduct1.jpg"));
        product2 = new Product(2L,"PRODUCT2", 2.0, 3, 4.3, "TYPE2", Collections.singletonList(loadPictureFrom + "defaultPictureProduct2.jpg"));
    }

    @Test
    @DisplayName("delete picture by name")
    void deletePicture() throws Exception {
        doNothing().when(productService).deleteProductPictureName("picture");
        mockMvc.perform(MockMvcRequestBuilders.delete(END_POINT + "/picture/delete/{id}" , "picture")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("edit picture throws product not found exception")
    void editPictureThrowsProductNotFoundException() throws Exception {
        MockMultipartFile pictureFile = new MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(END_POINT + "/upload/picture/{id}", 1L);
        when(productService.findProductById(1L)).thenThrow(ProductNotFoundException.class);
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });
        mockMvc.perform(builder
                .file("pictureFile", pictureFile.getBytes()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("delete picture by name throws product not found exception")
    void deletePictureThrowsProductNotFoundException() throws Exception {
        doThrow(ProductNotFoundException.class).when(productService).deleteProductPictureName(anyString());
        mockMvc.perform(MockMvcRequestBuilders.delete(END_POINT + "/picture/delete/{pictureShortName}" , anyString())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
