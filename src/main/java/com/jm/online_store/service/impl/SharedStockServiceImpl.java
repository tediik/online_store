package com.jm.online_store.service.impl;

import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.SharedStock;
import com.jm.online_store.repository.SharedStockRepository;
import com.jm.online_store.service.interf.SharedStockService;
import com.jm.online_store.service.interf.StockService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class SharedStockServiceImpl implements SharedStockService {
    private final SharedStockRepository sharedStockRepository;
    private final StockService stockService;
    private final UserService userService;

    @Override
    public List<SharedStock> findAll() {
        return sharedStockRepository.findAll();
    }

    @Override
    @Transactional
    public SharedStock addSharedStock(SharedStock sharedStock) {
        SharedStock sharedStockToAdd = SharedStock.builder()
                .stock(stockService.findStockById(sharedStock.getStock().getId()))
                .socialNetworkName(sharedStock.getSocialNetworkName())
                .user(userService.findById(sharedStock.getUser().getId()).orElseThrow(UserNotFoundException::new))
                .build();
        return sharedStockRepository.save(sharedStockToAdd);
    }

}
