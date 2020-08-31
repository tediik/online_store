package com.jm.online_store.service.impl;

import com.jm.online_store.model.SharedStocks;
import com.jm.online_store.repository.SharedStocksRepository;
import com.jm.online_store.service.interf.SharedStocksService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SharedStocksServiceImpl implements SharedStocksService {
    private final SharedStocksRepository sharedStocksRepository;

    @Override
    public List<SharedStocks> findAll() {
        return sharedStocksRepository.findAll();
    }

    @Override
    public SharedStocks addSharedStock(SharedStocks sharedStocks){
        return sharedStocksRepository.save(sharedStocks);
    }

}
