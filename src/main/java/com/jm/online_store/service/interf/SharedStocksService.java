package com.jm.online_store.service.interf;

import com.jm.online_store.model.SharedStocks;

import java.util.List;

public interface SharedStocksService {
    List<SharedStocks> findAll();

    SharedStocks addSharedStock(SharedStocks sharedStocks);
}
