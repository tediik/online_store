package com.jm.online_store.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jm.online_store.exception.ProductNotFoundException;
import com.jm.online_store.model.Product;
import com.jm.online_store.repository.ConfirmationTokenRepository;
import com.jm.online_store.repository.ProductRepository;
import com.jm.online_store.service.interf.CategoriesService;
import com.jm.online_store.service.interf.CommonSettingsService;
import com.jm.online_store.service.interf.EvaluationService;
import com.jm.online_store.service.interf.MailSenderService;
import com.jm.online_store.service.interf.ProductCharacteristicService;
import com.jm.online_store.service.interf.ProductService;
import com.jm.online_store.service.interf.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
class ProductServiceImplTest {
    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final EvaluationService evaluationService = mock(EvaluationService.class);
    private final UserService userService = mock(UserService.class);
    private final MailSenderService mailSenderService = mock(MailSenderService.class);
    private final CommonSettingsService commonSettingsService = mock(CommonSettingsService.class);
    private final CategoriesService categoriesService = mock(CategoriesService.class);
    private final ProductCharacteristicService productCharacteristicService = mock(ProductCharacteristicService.class);
    private final ConfirmationTokenRepository confirmationTokenRepository = mock(ConfirmationTokenRepository.class);
    private final ProductService productService = new ProductServiceImpl(productRepository, evaluationService, userService, commonSettingsService,
            mailSenderService, categoriesService, productCharacteristicService,confirmationTokenRepository);
    private Product product;
    private Set<String> subscribers;
    private Map<LocalDateTime, Double> prices;

    @BeforeEach
    void init() {
        product = new Product();
        subscribers = new HashSet<>();
        prices = new HashMap<>();
        subscribers.add("user@mail.ru");
        prices.put(LocalDateTime.now(),150D);
        product.setId(1L);
        product.setProduct("Ledger");
        product.setPrice(150D);
        product.setChangePriceHistory(prices);
        product.setPriceChangeSubscribers(subscribers);
        log.info("startup");
    }

    @Test
    void findProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.ofNullable(product));
        Optional<Product> optionalProduct = productService.findProductById(1L);
        assertNotNull(optionalProduct);
        verify(productRepository, times(1)).findById(product.getId());
    }

    @Test
    void findProductByName() {
        when(productRepository.findByProduct(product.getProduct())).thenReturn(Optional.ofNullable(product));
        Optional<Product> optionalProduct = productService.findProductByName(product.getProduct());
        assertNotNull(optionalProduct);
        verify(productRepository, times(1)).findByProduct(product.getProduct());
    }

    @Test
    void saveProduct() {
        when(productRepository.save(product)).thenReturn(product);
        Long id = productService.saveProduct(product);
        assertNotNull(id);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void deleteProduct() {
        when(productRepository.getOne(1L)).thenReturn(product);
        productService.deleteProduct(product.getId());
        assertEquals(true, product.getDeleteStatus());
        verify(productRepository, times(1)).save(product);
    }

    /**
     * метод тестирования добавления новых подписчиков на изменение цены
     */
    @Test
    void addNewSubscriberTest() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode newMail = objectMapper.createObjectNode();
        newMail.put("id", 1);
        newMail.put("email", "mail@mail.ru");

        ObjectNode existMail = objectMapper.createObjectNode();
        existMail.put("id", 1);
        existMail.put("email", "mail@mail.ru");

        when(productService.findProductById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);
        assertTrue(productService.addNewSubscriber(newMail));
        assertFalse(productService.addNewSubscriber(existMail));
    }

    /**
     * тест, того что метод добавления новых подписчиков бросет исключения
     */
    @Test
    void addNewSubscriberThrowsExceptionTest() {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode subscriberMail = objectMapper.createObjectNode();
        subscriberMail.put("id", 1L);
        subscriberMail.put("email", "mail@mail.ru");

        when(productService.findProductById(any())).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> productService.addNewSubscriber(subscriberMail));
    }

    /**
     * тест того, что метод изменения продукта бросает исключение
     */
    @Test
    void editProductThrowsExceptionTest() {
        when(productService.findProductById(1L)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> productService.editProduct(product));
    }

    /**
     * тест метода изменения товара
     */
    @Test
    void editProductTest() {
        Product updateProduct = new Product();
        updateProduct.setId(1L);
        updateProduct.setPrice(130D);
        prices.put(LocalDateTime.now(),130D);
        when(productService.findProductById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(1L);
        assertEquals(product.getPriceChangeSubscribers(),subscribers);
        assertEquals(product.getChangePriceHistory(),prices);
    }

    @AfterEach
    void after() {
        product = null;
        log.info("finalize");
    }
}