package com.jm.online_store.service.impl;

import com.jm.online_store.repository.FavouritesGroupRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class FavouritesGroupProductServiceImplTest {

    @MockBean
    private FavouritesGroupRepository favouritesGroupRepository;

}
