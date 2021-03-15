package com.jm.online_store.controller.rest.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jm.online_store.controller.ExceptionsHandler;
import com.jm.online_store.model.Product;
import com.jm.online_store.model.dto.ProductDto;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.service.interf.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductsRestControllerTest {
    private ProductService productService;
    private CategoriesService categoriesService;
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private final static String END_POINT = "/api/product";
    private List<Product> products;

    @BeforeEach
    void setUp() {
        categoriesService = mock(CategoriesService.class);
        productService = mock(ProductService.class);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ProductsRestController(productService, categoriesService))
                .setControllerAdvice(new ExceptionsHandler())
                .build();
        products = Arrays.asList(new Product(1L, "PRODUCT1", 1.0, 2, 2.3, "TYPE1"),
                                 new Product(2L,"PRODUCT2", 2.0, 3, 4.3, "TYPE2"));
    }

    @Test
    @DisplayName("handle file upload csv")
    void testHandleFileUploadCsv() throws Exception {
        MockMultipartFile fileCsv = new MockMultipartFile("file", "dummy.csv",
                "text/plain", "Some dataset...".getBytes());
        doNothing().when(productService).importFromCSVFile(fileCsv.getOriginalFilename(), 1L);
        mockMvc.perform(multipart(END_POINT + "/uploadFile/{id}", 1L)
                .file(fileCsv))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("handle file upload xml")
    void testHandleFileUploadXml() throws Exception {
        MockMultipartFile fileXml = new MockMultipartFile("file", "dummy.xml",
                "text/plain", "Some dataset...".getBytes());
        doNothing().when(productService).importFromCSVFile(fileXml.getOriginalFilename(), 1L);
        mockMvc.perform(multipart(END_POINT + "/uploadFile/{id}", 1L)
                .file(fileXml))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("find all products")
    void findAll() throws Exception {
        when(productService.findAll()).thenReturn(products);
        mockMvc.perform(get(END_POINT + "/getAll")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(products.size())))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        products.get(0).getId().intValue(),
                        products.get(1).getId().intValue())))
                .andExpect(jsonPath("$.data[*].product", containsInAnyOrder(
                        products.get(0).getProduct(),
                        products.get(1).getProduct())));
    }

    @Test
    @DisplayName("find all not deleted products")
    void getNotDeleteProducts() throws Exception {
        when(productService.getNotDeleteProducts()).thenReturn(products);
        mockMvc.perform(get(END_POINT + "/getNotDeletedProducts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(products.size())))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        products.get(0).getId().intValue(),
                        products.get(1).getId().intValue())))
                .andExpect(jsonPath("$.data[*].product", containsInAnyOrder(
                        products.get(0).getProduct(),
                        products.get(1).getProduct())));
    }

    @Test
    @DisplayName("find product by id")
    void findProductById() throws Exception {
        when(productService.getProductById(anyLong())).thenReturn(products.get(0));
        mockMvc.perform(get(END_POINT + "/manager/{id}", anyLong())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(products.get(0).getId()))
                .andExpect(jsonPath("$.data.product").value(products.get(0).getProduct()));
    }

    @Test
    @DisplayName("add product by id category")
    void addProduct() throws Exception {
        when(productService.saveProduct(any(Product.class))).thenReturn(products.get(0));
        doNothing().when(categoriesService).addToProduct(products.get(0), 1L);
        mockMvc.perform(post(END_POINT + "/add/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(toProductDto(products.get(0)))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(products.get(0).getId()))
                .andExpect(jsonPath("$.data.product").value(products.get(0).getProduct()));
    }

    @Test
    @DisplayName("edit product")
    void editProduct() throws Exception {
        when(productService.editProduct(any(Product.class))).thenReturn(products.get(1));
        mockMvc.perform(put(END_POINT + "/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(toProductDto(products.get(0)))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(products.get(1).getId()))
                .andExpect(jsonPath("$.data.product").value(products.get(1).getProduct()));
    }

    @Test
    @DisplayName("edit product")
    void editProductAndCategory() throws Exception {
        when(productService.editProduct(any(Product.class))).thenReturn(products.get(1));
        doNothing().when(categoriesService).addToProduct(any(Product.class), anyLong());
        mockMvc.perform(put(END_POINT + "/edit/{idOld}/{idNew}", -1L, anyLong())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(toProductDto(products.get(0)))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("edit product with remove from product")
    void editProductAndCategoryWithRemoveFromProduct() throws Exception {
        when(productService.editProduct(any(Product.class))).thenReturn(products.get(1));
        doNothing().when(categoriesService).removeFromProduct(products.get(0), 1L);
        mockMvc.perform(put(END_POINT + "/edit/{idOld}/{idNew}", 2L, anyLong())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(toProductDto(products.get(0)))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("delete product by id")
    void deleteProductById() throws Exception {
        doNothing().when(productService).deleteProduct(anyLong());
        mockMvc.perform(delete(END_POINT + "/{id}", anyLong())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("restore product by id")
    void restoreProductById() throws Exception {
        doNothing().when(productService).restoreProduct(anyLong());
        mockMvc.perform(post(END_POINT + "/restoredeleted/{id}", anyLong())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("filter by category with default category name and asc order")
    void filterByCategoryReturnsListWithAscOrder() throws Exception {
        when(productService.findAllOrderByRatingAsc()).thenReturn(products);
        mockMvc.perform(get(END_POINT + "/sort/{categoryName}/{orderSelect}", "default", "ascOrder")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(products.size())))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        products.get(0).getId().intValue(),
                        products.get(1).getId().intValue())))
                .andExpect(jsonPath("$.data[*].product", containsInAnyOrder(
                        products.get(0).getProduct(),
                        products.get(1).getProduct())));
    }

    @Test
    @DisplayName("filter by category with default category name and desc order")
    void filterByCategoryReturnsListWithDescOrder() throws Exception {
        when(productService.findAllOrderByRatingDesc()).thenReturn(products);
        mockMvc.perform(get(END_POINT + "/sort/{categoryName}/{orderSelect}", "default", "descOrder")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(products.size())))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        products.get(0).getId().intValue(),
                        products.get(1).getId().intValue())))
                .andExpect(jsonPath("$.data[*].product", containsInAnyOrder(
                        products.get(0).getProduct(),
                        products.get(1).getProduct())));
    }

    @Test
    @DisplayName("filter by category with custom category name asc order")
    void filterByCategoryReturnsListWithCustomCategoryNameAndAscOrder() throws Exception {
        when(productService.findProductsByCategoryName(anyString())).thenReturn(products);
        mockMvc.perform(get(END_POINT + "/sort/{categoryName}/{orderSelect}", "custom", "ascOrder")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(products.size())))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        products.get(0).getId().intValue(),
                        products.get(1).getId().intValue())))
                .andExpect(jsonPath("$.data[*].product", containsInAnyOrder(
                        products.get(0).getProduct(),
                        products.get(1).getProduct())));
    }

    @Test
    @DisplayName("filter by category with custom category name desc order")
    void filterByCategoryReturnsListWithCustomCategoryNameAndDescOrder() throws Exception {
        when(productService.findProductsByCategoryName(anyString())).thenReturn(products);
        mockMvc.perform(get(END_POINT + "/sort/{categoryName}/{orderSelect}", "custom", "descOrder")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(products.size())))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        products.get(0).getId().intValue(),
                        products.get(1).getId().intValue())))
                .andExpect(jsonPath("$.data[*].product", containsInAnyOrder(
                        products.get(0).getProduct(),
                        products.get(1).getProduct())));
    }

    @Test
    @DisplayName("filter by category with default category name desc order")
    void filterByCategoryInDescOrderWithDefaultCategoryName() throws Exception {
        when(productService.findAllOrderByRatingDesc()).thenReturn(products);
        mockMvc.perform(get(END_POINT + "/descOrder/{categoryName}",  "default")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(products.size())))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        products.get(0).getId().intValue(),
                        products.get(1).getId().intValue())))
                .andExpect(jsonPath("$.data[*].product", containsInAnyOrder(
                        products.get(0).getProduct(),
                        products.get(1).getProduct())));
    }

    @Test
    @DisplayName("filter by category with custom category name desc order")
    void filterByCategoryInDescOrderWithCustomCategoryName() throws Exception {
        when(productService.findProductsByCategoryName(anyString())).thenReturn(products);
        mockMvc.perform(get(END_POINT + "/descOrder/{categoryName}",  "custom")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(products.size())))
                .andExpect(jsonPath("$.data[*].id", containsInAnyOrder(
                        products.get(0).getId().intValue(),
                        products.get(1).getId().intValue())))
                .andExpect(jsonPath("$.data[*].product", containsInAnyOrder(
                        products.get(0).getProduct(),
                        products.get(1).getProduct())));
    }

    private ProductDto toProductDto(Product product) {
        return new ProductDto(product);
    }
}
