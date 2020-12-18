//package com.jm.online_store.service.impl;
//
//import com.jm.online_store.model.FavouritesGroup;
//import com.jm.online_store.model.Product;
//import com.jm.online_store.model.User;
//import com.jm.online_store.repository.FavouritesGroupRepository;
//import com.jm.online_store.service.interf.FavouritesGroupService;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//public class FavouritesGroupServiceImplTest {
//
//    @MockBean
//    private FavouritesGroupRepository favouritesGroupRepository;
//
//    @Autowired
//    FavouritesGroupService favouritesGroupService;
//
//    List<FavouritesGroup> all;
//    List<FavouritesGroup> findByUser;
//
//    Set<Product> set1;
//    Set<Product> set2;
//    Set<Product> set3;
//
//
//    FavouritesGroup group1;
//    FavouritesGroup group2;
//    FavouritesGroup group3;
//
//    Product product1;
//    Product product2;
//    Product product3;
//    Product product4;
//    Product product5;
//    Product product6;
//
//    User user1;
//    User user2;
//    User user3;
//
//    @BeforeEach
//    public void init() {
//
//        product1 = new Product("mac", 300, 10);
//        product2 = new Product("iphone", 100, 2);
//        set1 = new HashSet<>();
//        set1.add(product1);
//        set1.add(product2);
//        user1 = new User("user1@mail.ru", "1");
//        group1 = new FavouritesGroup("apple", user1, set1);
//        findByUser = new ArrayList<>();
//        findByUser.add(group1);
//
//        product3 = new Product("samsung1", 10, 3);
//        product4 = new Product("samsung2", 20, 90);
//        set2 = new HashSet<>();
//        set2.add(product3);
//        set2.add(product4);
//        user2 = new User("user2@mail.ru", "2");
//        group2 = new FavouritesGroup("Samsung", user2, set2);
//
//        product5 = new Product("e-book1", 200, 4);
//        product6 = new Product("e-book2,", 500, 90);
//        set3 = new HashSet<>();
//        set3.add(product5);
//        set3.add(product6);
//        user3 = new User("user3@mail.ru", "3");
//        group3 = new FavouritesGroup("Amazon", user3, set3);
//
//        all = new ArrayList<>();
//        all.add(group1);
//        all.add(group2);
//        all.add(group3);
//
//    }
//
//    @AfterEach
//    public void after() {
//
//        set1.clear();
//        set2.clear();
//        set3.clear();
//
//
//        group1 = null;
//        group2 = null;
//        group3 = null;
//
//        product1 = null;
//        product2 = null;
//        product3 = null;
//        product4 = null;
//        product5 = null;
//        product6 = null;
//
//        user1 = null;
//        user2 = null;
//        user3 = null;
//
//        all.clear();
//        findByUser.clear();
//    }
//
//    /**
//     * Find all test.
//     */
//    @Test
//    void findAllTest() {
//        when(favouritesGroupRepository.findAll()).thenReturn(all);
//        List<FavouritesGroup> list = favouritesGroupService.findAll();
//        assertEquals(3, list.size());
//        verify(favouritesGroupRepository, times(1)).findAll();
//    }
//
//    /**
//     * Find all by user test.
//     */
//    @Test
//    void findAllByUserTest() {
//        when(favouritesGroupRepository.findAllByUser(user1)).thenReturn(findByUser);
//        List<FavouritesGroup> list = favouritesGroupService.findAllByUser(user1);
//        assertEquals(list, findByUser);
//        verify(favouritesGroupRepository, times(1)).findAllByUser(user1);
//    }
//
//    /**
//     * Add product to favorite group test.
//     */
//    @Test
//    void addProductToFavoriteGroupTest() {
//        Set<Product> set = new HashSet<>();
//        set.add(product1);
//        FavouritesGroup someGroup = new FavouritesGroup();
//        favouritesGroupService.addProductToFavouritesGroup(product1, someGroup);
//        assertEquals(set, someGroup.getProducts());
//    }
//
//    /**
//     * Add favourites group test.
//     */
//    @Test
//    void addFavouritesGroupTest() {
//        when(favouritesGroupRepository.save(group1)).thenReturn(group1);
//        favouritesGroupService.save(group1);
//        verify(favouritesGroupRepository, times(1)).save(group1);
//    }
//
//    /**
//     * Delete specific product from specific favourites group test.
//     */
//    @Test
//    void deleteSpecificProductFromSpecificFavouritesGroupTest() {
//        FavouritesGroup group = new FavouritesGroup();
//        group.setProducts(set2);
//        favouritesGroupService.addProductToFavouritesGroup(product1, group);
//        favouritesGroupService.deleteSpecificProductFromSpecificFavouritesGroup(product1, group);
//        assertEquals(group2.getProducts(), group.getProducts());
//    }
//
//    /**
//     * Delete by id test.
//     */
//    @Test
//    void deleteByIdTest() {
//        doNothing().when(favouritesGroupRepository).deleteById(group2.getId());
//        favouritesGroupService.deleteById(group2.getId());
//        verify(favouritesGroupRepository, times(1)).deleteById(group2.getId());
//    }
//
//    /**
//     * Find by id test.
//     */
//    @Test
//    void findByIdTest() {
//        group3.setId(3L);
//        when(favouritesGroupRepository.findById(3L)).thenReturn(Optional.ofNullable(group3));
//        Optional<FavouritesGroup> testGroup = favouritesGroupService.findById(3L);
//        assertEquals(testGroup.get().getId(), group3.getId());
//        verify(favouritesGroupRepository, times(1)).findById(3L);
//    }
//
//    /**
//     * Find by name test.
//     */
//    @Test
//    void findByNameTest() {
//        when(favouritesGroupRepository.findByName("apple")).thenReturn(Optional.ofNullable(group1));
//        Optional<FavouritesGroup> groupOptional = favouritesGroupService.findByName("apple");
//        assertEquals(Optional.ofNullable(group1), groupOptional);
//        verify(favouritesGroupRepository, times(1)).findByName("apple");
//    }
//
//    /**
//     * Gets one favourites group by user and by name test.
//     */
//    @Test
//    void getOneFavouritesGroupByUserAndByNameTest() {
//        when(favouritesGroupRepository.getOneFavouritesGroupByUserAndByName(user3, "Amazon")).thenReturn(group3);
//        FavouritesGroup someGroup = favouritesGroupService.getOneFavouritesGroupByUserAndByName(user3, "Amazon");
//        assertEquals(group3, someGroup);
//        verify(favouritesGroupRepository, times(1)).getOneFavouritesGroupByUserAndByName(user3, "Amazon");
//    }
//
//    /**
//     * Gets product set test.
//     */
//    @Test
//    void getProductSetTest() {
//        Set<Product> someSet = favouritesGroupService.getProductSet(group3);
//        assertEquals(set3, someSet);
//    }
//
//}
